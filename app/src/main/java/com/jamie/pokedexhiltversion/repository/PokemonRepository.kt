package com.jamie.pokedexhiltversion.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.jamie.pokedexhiltversion.data.local.PokedexDatabase
import com.jamie.pokedexhiltversion.data.local.models.PokemonListEntity
import com.jamie.pokedexhiltversion.data.remote.PokeApi
import com.jamie.pokedexhiltversion.data.remote.responses.Pokemon
import com.jamie.pokedexhiltversion.data.remote.responses.evolution.EvolutionChain
import com.jamie.pokedexhiltversion.data.remote.responses.evolution.PokemonSpecies
import com.jamie.pokedexhiltversion.paging.PokemonRemoteMediator
import com.jamie.pokedexhiltversion.pokemonlist.SortType
import com.jamie.pokedexhiltversion.util.Constants.PAGE_SIZE
import com.jamie.pokedexhiltversion.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PokemonRepository @Inject constructor(
    private val api: PokeApi,
    private val db: PokedexDatabase
) {
    private val pokemonDao = db.pokemonDao()

    @OptIn(ExperimentalPagingApi::class)
    fun getPokemonList(
        searchQuery: String,
        sortType: SortType,
        selectedTypes: List<String>
    ): Flow<PagingData<PokemonListEntity>> {
        val query = buildFilteredQuery(searchQuery, sortType, selectedTypes)

        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = if (searchQuery.isBlank() && selectedTypes.isEmpty()) {
                PokemonRemoteMediator(pokeApi = api, pokedexDatabase = db)
            } else {
                null // Disable remote mediator when filtering/searching
            },
            pagingSourceFactory = { pokemonDao.getFilteredPokemonList(query) }
        ).flow
    }

    private fun buildFilteredQuery(
        searchQuery: String,
        sortType: SortType,
        selectedTypes: List<String>
    ): SimpleSQLiteQuery {
        val sb = StringBuilder("SELECT * FROM pokemon_list WHERE ")

        // Search query part
        if (searchQuery.isNotBlank()) {
            sb.append("(pokemonName LIKE ?)")
        } else {
            sb.append("1=1") // Always true if no search query
        }

        // Type filtering part
        if (selectedTypes.isNotEmpty()) {
            sb.append(" AND (")
            selectedTypes.forEachIndexed { index, type ->
                sb.append("types LIKE ?")
                if (index < selectedTypes.size - 1) {
                    sb.append(" OR ")
                }
            }
            sb.append(")")
        }

        // Sorting part
        sb.append(" ORDER BY ")
        sb.append(
            when (sortType) {
                SortType.NUMBER -> "number ASC"
                SortType.NAME -> "pokemonName ASC"
                SortType.HP -> "hp DESC"
                SortType.ATTACK -> "attack DESC"
                SortType.DEFENSE -> "defense DESC"
            }
        )

        val args = mutableListOf<Any>()
        if (searchQuery.isNotBlank()) {
            args.add("%$searchQuery%")
        }
        if (selectedTypes.isNotEmpty()) {
            selectedTypes.forEach { type ->
                args.add("%$type%")
            }
        }

        return SimpleSQLiteQuery(sb.toString(), args.toTypedArray())
    }


    fun getPokemonInfo(pokemonName: String): Flow<Resource<Pokemon>> {
        return object : Flow<Resource<Pokemon>> {
            override suspend fun collect(collector: kotlinx.coroutines.flow.FlowCollector<Resource<Pokemon>>) {
                val cachedData = pokemonDao.getPokemon(pokemonName).first()?.pokemonInfo
                collector.emit(Resource.Loading(cachedData))
                try {
                    val response = api.getPokemonInfo(pokemonName)
                    pokemonDao.updatePokemonInfo(pokemonName, response)
                    val newData = pokemonDao.getPokemon(pokemonName).first()?.pokemonInfo
                    if (newData != null) {
                        collector.emit(Resource.Success(newData))
                    } else {
                        collector.emit(Resource.Error("Data not found after fetch.", cachedData))
                    }
                } catch (e: Exception) {
                    collector.emit(Resource.Error("Network error: ${e.message}", cachedData))
                }
            }
        }
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