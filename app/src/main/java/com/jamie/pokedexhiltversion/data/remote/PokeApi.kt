package com.jamie.pokedexhiltversion.data.remote

import com.jamie.pokedexhiltversion.data.remote.responses.Pokemon
import com.jamie.pokedexhiltversion.data.remote.responses.PokemonList
import com.jamie.pokedexhiltversion.data.remote.responses.evolution.EvolutionChain
import com.jamie.pokedexhiltversion.data.remote.responses.evolution.PokemonSpecies
import com.jamie.pokedexhiltversion.data.remote.responses.move.MoveDetail
import com.jamie.pokedexhiltversion.data.remote.responses.move.MoveList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface PokeApi {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ) : PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemonInfo(
        @Path("name") name: String
    ) : Pokemon

    @GET("pokemon-species/{name}")
    suspend fun getPokemonSpecies(
        @Path("name") name: String
    ): PokemonSpecies

    @GET
    suspend fun getEvolutionChain(
        @Url url: String
    ): EvolutionChain

    @GET("move")
    suspend fun getMoveList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): MoveList

    @GET("move/{name}")
    suspend fun getMoveInfo(
        @Path("name") name: String
    ): MoveDetail
}