import com.patrickhoette.pokebe.entity.core.error.RuleFailedError
import com.patrickhoette.pokebe.entity.core.error.ValidationFailedError
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

sealed interface Validation<in T, out R> {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): R

    class NullValidation <in T, out R> : Validation<T, R?> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): R? = null
    }

    class ValueValidation<in T, out R>(
        private val name: String,
        private val input: T,
        private val builder: ValidationScope.(ValidationStep<T>) -> ValidationStep<R>,
    ) : Validation<T, R> {

        private var result by atomic<ValidationResult<R>>(NotInitialized)

        override fun getValue(
            thisRef: Any?,
            property: KProperty<*>,
        ): R = when (val backing = result) {
            is Completed -> backing.result
            is NotInitialized -> throw IllegalStateException("Result has not yet been compute, run Validator.run first")
        }

        suspend operator fun invoke() {
            val start = ValidationStart(input)

            val steps = object : ValidationScope() {}.builder(start)

            result = Completed(steps.fold(name))
        }
    }
}

@ValidationDsl
abstract class ValidationScope {

    infix fun <T : Comparable<T>> ValidationStep<T>.atLeast(
        other: T,
    ): ValidationStep<T> = ValidationPart(
        hintBody = "must be at least $other",
        previous = this,
    ) {
        if (it >= other) Success(it) else Failed
    }

    infix fun <T : Comparable<T>> ValidationStep<T>.atMost(
        other: T,
    ): ValidationStep<T> = ValidationPart(
        hintBody = "must be at most $other",
        previous = this,
    ) {
        if (it <= other) Success(it) else Failed
    }

    infix fun <T : Comparable<T>> ValidationStep<T>.inRange(
        range: ClosedRange<T>,
    ): ValidationStep<T> = ValidationPart(
        hintBody = "must be in range $range",
        previous = this,
    ) {
        if (it in range) Success(it) else Failed
    }

    infix fun ValidationStep<String>.equals(
        other: String,
    ): ValidationStep<String> = ValidationPart(
        hintBody = "must match '$other'",
        previous = this,
    ) {
        if (it == other) Success(it) else Failed
    }

    infix fun ValidationStep<String>.equalsIgnoringCase(
        other: String,
    ): ValidationStep<String> = ValidationPart(
        hintBody = "must match (case insensitive) '$other'",
        previous = this,
    ) {
        if (it == other) Success(it) else Failed
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

class Validator {

    private val validations = atomic(emptyList<ValueValidation<*, *>>())

    @ValidationDsl
    fun <T, R> validate(
        name: String,
        input: T?,
        builder: @ValidationDsl ValidationScope.(ValidationStep<T>) -> ValidationStep<R>,
    ): Validation<T, R?> = if (input == null) {
        NullValidation()
    } else {
        val validation = ValueValidation<T, R>(name, input, builder)
        validations.update { it + validation }
        validation
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
