package com.patrickhoette.pokebe.pokemon.api.list.response

import com.patrickhoette.pokebe.pokemon.api.list.PokemonListController
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.pokemonListRoute() {
    get("/v1/pokemon/") {
        val pokemonListController by inject<PokemonListController>()
    }
}
