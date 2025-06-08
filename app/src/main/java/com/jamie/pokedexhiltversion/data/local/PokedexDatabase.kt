package com.jamie.pokedexhiltversion.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jamie.pokedexhiltversion.data.local.converters.Converters
import com.jamie.pokedexhiltversion.data.local.models.PokemonListEntity
import com.jamie.pokedexhiltversion.data.local.models.RemoteKeys

@Database(
    entities = [PokemonListEntity::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PokedexDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}