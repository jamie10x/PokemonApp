package com.jamie.pokedexhiltversion.data.remote.responses.move

import com.google.gson.annotations.SerializedName
import com.jamie.pokedexhiltversion.data.remote.responses.Type

data class MoveDetail(
    val id: Int,
    val name: String,
    val accuracy: Int?,
    val power: Int?,
    val pp: Int,
    @SerializedName("flavor_text_entries")
    val flavorTextEntries: List<FlavorTextEntry>,
    val type: Type
)

data class FlavorTextEntry(
    @SerializedName("flavor_text")
    val flavorText: String,
    val language: Language
)

data class Language(
    val name: String,
    val url: String
)