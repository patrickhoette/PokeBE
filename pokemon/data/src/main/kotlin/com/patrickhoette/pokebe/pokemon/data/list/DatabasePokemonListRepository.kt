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

package com.patrickhoette.pokebe.pokemon.data.list

import com.patrickhoette.pokebe.database.Database
import com.patrickhoette.pokebe.entity.core.Ordering
import com.patrickhoette.pokebe.entity.core.Ordering.*
import com.patrickhoette.pokebe.entity.core.Ordering.Ascending
import com.patrickhoette.pokebe.entity.pokemon.list.PokemonList
import com.patrickhoette.pokebe.entity.pokemon.list.PokemonListItem
import com.patrickhoette.pokebe.entity.pokemon.list.PokemonSorting
import com.patrickhoette.pokebe.entity.pokemon.list.PokemonSorting.*
import com.patrickhoette.pokebe.entity.pokemon.list.PokemonSorting.Natural
import com.patrickhoette.pokebe.pokemon.domain.list.PokemonListRepository
import org.koin.core.annotation.Factory

@Factory
class DatabasePokemonListRepository(
    private val database: Database,
    private val mapper: PokemonListMapper,
) : PokemonListRepository {

    override suspend fun getPokemonList(
        pageSize: Int,
        page: Int,
        sorting: PokemonSorting,
        ordering: Ordering,
    ): PokemonList {
        val sortColumn = when (sorting) {
            Id -> "p.id"
            Natural -> "p.natural_order"
        }
        val orderDirection = when (ordering) {
            Ascending -> "ASC"
            Descending -> "DESC"
        }

        val list = database.executeAsList(
            query = """
            |SELECT
            |    p.id AS id,
            |    p.name AS name,
            |    pos.sprite_path AS path,
            |    p.primarytype,
            |    p.secondarytype
            |FROM pokemon p
            |JOIN pokemon_sprite ps ON ps.pokemon_id = p.id
            |JOIN pokemon_official_sprite pos ON pos.sprite_path = ps.path
            |WHERE pos.is_shiny = FALSE
            |ORDER BY $sortColumn $orderDirection
            |LIMIT ?
            |OFFSET ?;
        """.trimMargin(),
            pageSize + 1,
            page * pageSize,
            mapper = mapper::mapToPokemonListItem,
        )

        return PokemonList(
            hasNext = list.size > pageSize,
            pageSize = pageSize,
            page = page,
            list = list.take(pageSize),
        )
    }

}
