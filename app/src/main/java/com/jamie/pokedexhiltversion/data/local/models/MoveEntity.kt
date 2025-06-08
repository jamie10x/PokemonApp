package com.jamie.pokedexhiltversion.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moves")
data class MoveEntity(
    @PrimaryKey
    val name: String,
    val url: String
)