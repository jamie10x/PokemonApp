package com.jamie.pokedexhiltversion.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jamie.pokedexhiltversion.data.remote.responses.Pokemon
import com.jamie.pokedexhiltversion.data.remote.responses.Type

@Entity(tableName = "pokemon_list")
data class PokemonListEntity(
    @PrimaryKey
    val pokemonName: String,
    val imageUrl: String,
    val number: Int,
    val isFavorite: Boolean = false,
    val pokemonInfo: Pokemon? = null,
    val types: List<String> = emptyList(),
    val hp: Int = 0,
    val attack: Int = 0,
    val defense: Int = 0,
    val specialAttack: Int = 0,
    val specialDefense: Int = 0,
    val speed: Int = 0
)