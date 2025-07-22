package com.patrickhoette.pokebe.pokemon.api.list.response

import com.patrickhoette.pokebe.entity.pokemon.generic.Type
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonListResponse(

    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("imageUrl")
    val imageUrl: String,

    @SerialName("primaryType")
    val primaryType: Type,

    @SerialName("secondaryType")
    val secondaryType: Type?,
)
