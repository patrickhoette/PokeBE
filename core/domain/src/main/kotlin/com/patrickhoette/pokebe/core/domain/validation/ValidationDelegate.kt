/*
 * Copyright 2025 Patrick Hoette
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the “Software”), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

@file:Suppress("unused")

package com.patrickhoette.pokebe.core.domain.validation

import com.patrickhoette.pokebe.core.domain.validation.ValidationDelegate.OptionalValidationDelegate
import com.patrickhoette.pokebe.core.domain.validation.ValidationDelegate.RequiredValidationDelegate
import com.patrickhoette.pokebe.core.domain.validation.ValidationResult.Completed
import com.patrickhoette.pokebe.core.domain.validation.ValidationResult.NotInitialized
import com.patrickhoette.pokebe.core.domain.validation.ValidationStepResult.Failed
import com.patrickhoette.pokebe.core.domain.validation.ValidationStepResult.Success
import com.patrickhoette.pokebe.entity.core.error.RuleFailedError
import com.patrickhoette.pokebe.entity.core.error.ValidationFailedError
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

sealed class ValidationStepResult<out R> {

    data object Failed : ValidationStepResult<Nothing>()

    data class Success<out T>(val value: T) : ValidationStepResult<T>()
}

interface ValidationStep<out R> {

    suspend fun fold(name: String): R
}

class ValidationStart<T>(private val input: T) : ValidationStep<T> {

    override suspend fun fold(name: String): T = input
}

data class ValidationPart<in T, out R>(
    private val hintBody: String,
    private val previous: ValidationStep<T>,
    private val rule: suspend (T) -> ValidationStepResult<R>,
) : ValidationStep<R> {

    override suspend fun fold(name: String): R {
        val prevResult = previous.fold(name)
        val result = rule(prevResult)
        return when (result) {
            is Failed -> throw RuleFailedError("$name $hintBody, but instead was: '$prevResult'")
            is Success -> result.value
        }
    }
}

sealed class ValidationResult<out T> {

    data class Completed<T>(val result: T) : ValidationResult<T>()

    data object NotInitialized : ValidationResult<Nothing>()
}

sealed class ValidationDelegate<T, R>(
    protected val input: T?,
    protected val builder: ValidationScope.(ValidationStep<T>) -> ValidationStep<R>,
) : ReadOnlyProperty<Any?, R> {

    protected lateinit var name: String
    protected var result by atomic<ValidationResult<R>>(NotInitialized)

    operator fun provideDelegate(thisRef: Any, property: KProperty<*>): ReadOnlyProperty<Any, R> {
        name = property.name
        return this
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): R {
        return when (val backing = result) {
            is Completed -> backing.result
            is NotInitialized -> throw IllegalStateException("Result has not yet been compute, run Validator.run first")
        }
    }

    abstract suspend operator fun invoke()

    class OptionalValidationDelegate<T, R>(
        input: T?,
        builder: ValidationScope.(ValidationStep<T>) -> ValidationStep<R>,
    ) : ValidationDelegate<T, R>(input, builder) {

        override suspend operator fun invoke() {
        }
    }

    class RequiredValidationDelegate<T, R>(
        input: T?,
        builder: ValidationScope.(ValidationStep<T>) -> ValidationStep<R>,
    ) : ValidationDelegate<T, R>(input, builder) {

        override suspend operator fun invoke() {
            if (input == null) throw RuleFailedError("$name should not be null")

            val start = ValidationStart(input)

            val steps = object : ValidationScope() {}.builder(start)

            result = Completed(steps.fold(name))
        }
    }
}

@ValidationDsl
abstract class ValidationScope {

    fun <T : Comparable<T>> ValidationStep<T>.atLeast(
        other: T,
    ): ValidationStep<T> = ValidationPart(
        hintBody = "must be at least $other",
        previous = this,
    ) {
        if (it >= other) Success(it) else Failed
    }

    fun <T : Comparable<T>> ValidationStep<T>.atMost(
        other: T,
    ): ValidationStep<T> = ValidationPart(
        hintBody = "must be at most $other",
        previous = this,
    ) {
        if (it <= other) Success(it) else Failed
    }

    fun <T : Comparable<T>> ValidationStep<T>.inRange(
        range: ClosedRange<T>,
    ): ValidationStep<T> = ValidationPart(
        hintBody = "must be in range $range",
        previous = this,
    ) {
        if (it in range) Success(it) else Failed
    }

    fun ValidationStep<String>.matches(
        other: String,
        ignoreCase: Boolean = false,
    ): ValidationStep<String> = ValidationPart(
        hintBody = buildString {
            append("must match '$other'")
            if (ignoreCase) append(" ignoring case")
        },
        previous = this,
    ) {
        if (it.equals(other, ignoreCase)) Success(it) else Failed
    }

    fun ValidationStep<String>.int(): ValidationStep<Int> = ValidationPart(
        hintBody = "must be an integer",
        previous = this,
    ) {
        it.toIntOrNull()?.let(::Success) ?: Failed
    }

    fun ValidationStep<String>.long(): ValidationStep<Long> = ValidationPart(
        hintBody = "must be an long",
        previous = this,
    ) {
        it.toLongOrNull()?.let(::Success) ?: Failed
    }

    fun ValidationStep<String>.float(): ValidationStep<Float> = ValidationPart(
        hintBody = "must be an float",
        previous = this,
    ) {
        it.toFloatOrNull()?.let(::Success) ?: Failed
    }

    fun ValidationStep<String>.double(): ValidationStep<Double> = ValidationPart(
        hintBody = "must be a double",
        previous = this,
    ) {
        it.toDoubleOrNull()?.let(::Success) ?: Failed
    }

    fun ValidationStep<String>.boolean(): ValidationStep<Boolean> = ValidationPart(
        hintBody = "must be a boolean",
        previous = this,
    ) {
        it.lowercase().toBooleanStrictOrNull()?.let(::Success) ?: Failed
    }

    inline fun <reified T : Enum<T>> ValidationStep<String>.entryIn(): ValidationStep<T> {
        val allowedOptions = enumValues<T>().map { it.name.lowercase() }
        return ValidationPart(
            hintBody = "is not a part of allowed options $allowedOptions",
            previous = this,
        ) {
            if (it in allowedOptions) Success(enumValueOf(it)) else Failed
        }
    }
}

@ValidationDsl
class Validator {

    private val validations = atomic(emptyList<ValidationDelegate<*, *>>())

    fun <T, R> validateRequired(
        input: T?,
        builder: ValidationScope.(ValidationStep<T>) -> ValidationStep<R>,
    ): RequiredValidationDelegate<T, R> = RequiredValidationDelegate(input, builder)
        .also { validation ->
            validations.update { it + validation }
        }

    fun <T, R> validateOptional(
        input: T?,
        builder: ValidationScope.(ValidationStep<T>) -> ValidationStep<R?>,
    ): OptionalValidationDelegate<T, R?> = OptionalValidationDelegate(input, builder)
        .also { validation ->
            validations.update { it + validation }
        }

    suspend fun run() = coroutineScope {
        val failures = validations.value
            .map {
                async {
                    try {
                        it()
                        null
                    } catch (error: RuleFailedError) {
                        error
                    }
                }
            }
            .awaitAll()
            .filterNotNull()

        if (failures.isNotEmpty()) throw ValidationFailedError(failures)
    }
}
