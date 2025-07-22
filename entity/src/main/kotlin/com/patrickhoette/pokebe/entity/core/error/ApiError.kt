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

package com.patrickhoette.pokebe.entity.core.error

import com.patrickhoette.pokebe.entity.core.error.ErrorCode.DevDoneGoofed

sealed class ApiError : Exception {

    val errorCode: ErrorCode?

    constructor(errorCode: ErrorCode? = null) : super() {
        this.errorCode = errorCode
    }

    constructor(message: String, errorCode: ErrorCode? = null) : super(message)  {
        this.errorCode = errorCode
    }

    constructor(cause: Throwable, errorCode: ErrorCode? = null) : super(cause) {
        this.errorCode = errorCode
    }

    constructor(message: String, cause: Throwable, errorCode: ErrorCode? = null) : super(message, cause) {
        this.errorCode = errorCode
    }

    abstract override fun equals(other: Any?): Boolean

    abstract override fun hashCode(): Int

    class InternalServerError : ApiError {

        constructor() : super(DevDoneGoofed)

        constructor(message: String) : super(message, DevDoneGoofed)

        constructor(cause: Throwable) : super(cause, DevDoneGoofed)

        constructor(message: String, cause: Throwable) : super(message, cause, DevDoneGoofed)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is InternalServerError) return false
            return cause == other.cause && message == other.message && errorCode == other.errorCode
        }

        override fun hashCode(): Int {
            var result = message?.hashCode() ?: 0
            result = 31 * result + (cause?.hashCode() ?: 0)
            result = 31 * result + (errorCode?.hashCode() ?: 0)
            return result
        }
    }

    class NotImplementerError : ApiError {

        constructor(errorCode: ErrorCode? = null) : super(errorCode)

        constructor(message: String, errorCode: ErrorCode? = null) : super(message, errorCode)

        constructor(cause: Throwable, errorCode: ErrorCode? = null) : super(cause, errorCode)

        constructor(message: String, cause: Throwable, errorCode: ErrorCode? = null) : super(message, cause, errorCode)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is NotImplementerError) return false
            return cause == other.cause && message == other.message && errorCode == other.errorCode
        }

        override fun hashCode(): Int {
            var result = message?.hashCode() ?: 0
            result = 31 * result + (cause?.hashCode() ?: 0)
            result = 31 * result + (errorCode?.hashCode() ?: 0)
            return result
        }
    }

    open class BadRequestError : ApiError {

        constructor(errorCode: ErrorCode? = null) : super(errorCode)

        constructor(message: String, errorCode: ErrorCode? = null) : super(message, errorCode)

        constructor(cause: Throwable, errorCode: ErrorCode? = null) : super(cause, errorCode)

        constructor(message: String, cause: Throwable, errorCode: ErrorCode? = null) : super(message, cause, errorCode)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is BadRequestError) return false
            return cause == other.cause && message == other.message && errorCode == other.errorCode
        }

        override fun hashCode(): Int {
            var result = message?.hashCode() ?: 0
            result = 31 * result + (cause?.hashCode() ?: 0)
            result = 31 * result + (errorCode?.hashCode() ?: 0)
            return result
        }
    }

    open class NotFoundError : ApiError {

        constructor(errorCode: ErrorCode? = null) : super(errorCode)

        constructor(message: String, errorCode: ErrorCode? = null) : super(message, errorCode)

        constructor(cause: Throwable, errorCode: ErrorCode? = null) : super(cause, errorCode)

        constructor(message: String, cause: Throwable, errorCode: ErrorCode? = null) : super(message, cause, errorCode)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is NotFoundError) return false
            return cause == other.cause && message == other.message && errorCode == other.errorCode
        }

        override fun hashCode(): Int {
            var result = message?.hashCode() ?: 0
            result = 31 * result + (cause?.hashCode() ?: 0)
            result = 31 * result + (errorCode?.hashCode() ?: 0)
            return result
        }
    }
}
