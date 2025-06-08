package com.jamie.pokedexhiltversion.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val pokemonName: String,
    val prevKey: Int?,
    val nextKey: Int?
)