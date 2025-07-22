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

package com.patrickhoette.pokebe.pokemon.api.list

import com.patrickhoette.pokebe.core.api.StandardQueryParameters.Order
import com.patrickhoette.pokebe.core.api.StandardQueryParameters.Page
import com.patrickhoette.pokebe.core.api.StandardQueryParameters.PageSize
import com.patrickhoette.pokebe.core.api.StandardQueryParameters.Sort
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject

fun Route.pokemonListRoute() {
    val controller by inject<PokemonListController>()
    get("/v1/pokemon/") {
        val pageSize = call.request.queryParameters[PageSize]
        val page = call.request.queryParameters[Page]
        val sort = call.request.queryParameters[Sort]
        val order = call.request.queryParameters[Order]

        call.respond(
            controller.onPokemonList(
                pageSizeParam = pageSize,
                pageParam = page,
                sortParam = sort,
                orderParam = order,
            )
        )
    }
}
