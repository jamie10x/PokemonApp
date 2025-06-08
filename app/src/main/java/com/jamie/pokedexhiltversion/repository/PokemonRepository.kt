package com.jamie.pokedexhiltversion.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jamie.pokedexhiltversion.data.local.PokedexDatabase
import com.jamie.pokedexhiltversion.data.local.models.PokemonListEntity
import com.jamie.pokedexhiltversion.data.remote.PokeApi
import com.jamie.pokedexhiltversion.data.remote.responses.Pokemon
import com.jamie.pokedexhiltversion.data.remote.responses.evolution.EvolutionChain
import com.jamie.pokedexhiltversion.data.remote.responses.evolution.PokemonSpecies
import com.jamie.pokedexhiltversion.paging.PokemonRemoteMediator
import com.jamie.pokedexhiltversion.util.Constants.PAGE_SIZE
import com.jamie.pokedexhiltversion.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PokemonRepository @Inject constructor(
    private val api: PokeApi,
    private val db: PokedexDatabase
) {
    private val pokemonDao = db.pokemonDao()

    @OptIn(ExperimentalPagingApi::class)
    fun getPokemonList(): Flow<PagingData<PokemonListEntity>> {
        val pagingSourceFactory = { pokemonDao.getPokemonList() }

        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = PokemonRemoteMediator(
                pokeApi = api,
                pokedexDatabase = db
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun getFavoritePokemonList(): Flow<PagingData<PokemonListEntity>> {
        val pagingSourceFactory = { pokemonDao.getFavoritePokemonList() }
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun searchPokemonList(query: String): Flow<PagingData<PokemonListEntity>> {
        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { pokemonDao.searchPokemonList(dbQuery) }

        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfo(pokemonName)
        } catch(e: Exception) {
            return Resource.Error("An unknown error occurred.")
        }
        return Resource.Success(response)
    }

    fun getPokemonFromDb(pokemonName: String): Flow<PokemonListEntity?> {
        return pokemonDao.getPokemon(pokemonName)
    }

    suspend fun setFavorite(pokemonName: String, isFavorite: Boolean) {
        pokemonDao.setFavorite(pokemonName, isFavorite)
    }

    suspend fun getPokemonSpecies(pokemonName: String): Resource<PokemonSpecies> {
        val response = try {
            api.getPokemonSpecies(pokemonName)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred.")
        }
        return Resource.Success(response)
    }

    suspend fun getEvolutionChain(url: String): Resource<EvolutionChain> {
        val response = try {
            api.getEvolutionChain(url)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred.")
        }
        return Resource.Success(response)
    }
}