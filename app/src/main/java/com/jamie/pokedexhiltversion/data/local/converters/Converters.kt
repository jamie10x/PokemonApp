package com.jamie.pokedexhiltversion.data.local.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jamie.pokedexhiltversion.data.remote.responses.Pokemon

@ProvidedTypeConverter
class Converters(private val gson: Gson) {
    @TypeConverter
    fun fromPokemon(pokemon: Pokemon?): String? {
        return gson.toJson(pokemon)
    }

    @TypeConverter
    fun toPokemon(pokemonString: String?): Pokemon? {
        if (pokemonString.isNullOrBlank()) {
            return null
        }
        return gson.fromJson(pokemonString, Pokemon::class.java)
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(string: String?): List<String>? {
        if (string.isNullOrBlank()) {
            return emptyList()
        }
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(string, listType)
    }
}