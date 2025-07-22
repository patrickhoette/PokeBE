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

import com.patrickhoette.pokebe.core.api.StandardQueryParameters.Sort
import com.patrickhoette.pokebe.core.api.validation.validateOrder
import com.patrickhoette.pokebe.core.api.validation.validatePage
import com.patrickhoette.pokebe.core.api.validation.validatePageSize
import com.patrickhoette.pokebe.core.domain.validation.Validator
import com.patrickhoette.pokebe.entity.core.Ordering.Ascending
import com.patrickhoette.pokebe.entity.pokemon.list.PokemonSorting
import com.patrickhoette.pokebe.entity.pokemon.list.PokemonSorting.Id
import com.patrickhoette.pokebe.pokemon.api.list.response.PokemonListResponse
import com.patrickhoette.pokebe.pokemon.domain.list.GetPokemonList
import org.koin.core.annotation.Factory

@Factory
class PokemonListController(
    private val getPokemonList: GetPokemonList,
    private val mapper: PokemonListApiMapper,
) {

    suspend fun onPokemonList(
        pageSizeParam: String?,
        pageParam: String?,
        sortParam: String?,
        orderParam: String?,
    ): PokemonListResponse {
        val validator = Validator()

        val pageSize by validator.validatePageSize(pageSizeParam)
        val page by validator.validatePage(pageParam)
        val sort by validator.validateOptional(sortParam) { it.entryIn<PokemonSorting>() }
        val order by validator.validateOrder(orderParam)

        validator.run()

        val models = getPokemonList(
            pageSize = pageSize ?: 40,
            page = page ?: 0,
            sorting = sort ?: Id,
            ordering = order ?: Ascending,
        )

        return mapper.mapToResponse(models)
    }
}
