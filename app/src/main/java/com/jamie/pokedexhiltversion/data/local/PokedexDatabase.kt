package com.jamie.pokedexhiltversion.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jamie.pokedexhiltversion.data.local.models.PokemonListEntity
import com.jamie.pokedexhiltversion.data.local.models.RemoteKeys

@Database(
    entities = [PokemonListEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class PokedexDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}