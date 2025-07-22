package com.patrickhoette.pokebe.entity.core.error

import com.patrickhoette.pokebe.entity.core.error.ApiError.InternalServerError

data class ValidationFailedError(
    val failures: List<Throwable>,
) : Exception()
