package com.jamie.pokedexhiltversion.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_list")
data class PokemonListEntity(
    @PrimaryKey
    val pokemonName: String,
    val imageUrl: String,
    val number: Int,
    val isFavorite: Boolean = false
)