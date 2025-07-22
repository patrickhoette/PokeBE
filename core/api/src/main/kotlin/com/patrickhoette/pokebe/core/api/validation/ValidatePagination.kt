package com.patrickhoette.pokebe.core.api.validation

import com.patrickhoette.pokebe.core.domain.validation.Validator

fun Validator.Builder.validatePagination(pageSize: String?, page: String?) {
    pageSize?.let {
        it.isInt() hint "Page size is not a valid integer, instead it is: '$pageSize'"
        it.toInt() inRange 1..200 hint "Page size must be at least 1 and at most 200, but was: '$pageSize'"
    }

    page?.let {
        it.isInt() hint "Page is not a valid integer, instead it is: '$page'"
        it.toInt() atLeast 0 hint "Page must not be negative, but was: '$page'"
    }
}
