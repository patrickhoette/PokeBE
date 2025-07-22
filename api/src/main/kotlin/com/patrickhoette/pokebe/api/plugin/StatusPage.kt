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

package com.patrickhoette.pokebe.api.plugin

import com.patrickhoette.pokebe.core.api.response.ErrorResponse
import com.patrickhoette.pokebe.entity.core.error.ApiError
import com.patrickhoette.pokebe.entity.core.error.ApiError.BadRequestError
import com.patrickhoette.pokebe.entity.core.error.ApiError.InternalServerError
import com.patrickhoette.pokebe.entity.core.error.ApiError.NotFoundError
import com.patrickhoette.pokebe.entity.core.error.ApiError.NotImplementerError
import com.patrickhoette.pokebe.entity.core.error.ErrorCode.DevDoneGoofed
import com.patrickhoette.pokebe.entity.core.error.ValidationFailedError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureStatusPage() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            val status = mapToErrorStatus(cause)

            call.respond(
                status = mapToErrorStatus(cause),
                message = mapToErrorResponse(cause, status),
            )
        }
    }
}

private fun mapToErrorResponse(error: Throwable, status: HttpStatusCode) = when (error) {
    is ValidationFailedError -> ErrorResponse(
        code = status.value,
        messages = error.failures.mapNotNull { it.message },
    )
    is ApiError -> ErrorResponse(
        code = status.value,
        errorCode = error.errorCode,
        messages = listOfNotNull(error.message),
    )
    else -> ErrorResponse(code = status.value, errorCode = DevDoneGoofed, messages = listOfNotNull(error.message))
}

private fun mapToErrorStatus(error: Throwable): HttpStatusCode {
    if (error is ValidationFailedError) return HttpStatusCode.BadRequest
    if (error !is ApiError) return HttpStatusCode.InternalServerError

    return when (error) {
        is BadRequestError -> HttpStatusCode.BadRequest
        is InternalServerError -> HttpStatusCode.InternalServerError
        is NotFoundError -> HttpStatusCode.NotFound
        is NotImplementerError -> HttpStatusCode.NotImplemented
    }
}
