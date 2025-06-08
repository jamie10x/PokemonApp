package com.jamie.pokedexhiltversion.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.jamie.pokedexhiltversion.data.local.PokedexDatabase
import com.jamie.pokedexhiltversion.data.local.models.PokemonListEntity
import com.jamie.pokedexhiltversion.data.local.models.RemoteKeys
import com.jamie.pokedexhiltversion.data.remote.PokeApi
import com.jamie.pokedexhiltversion.util.Constants.PAGE_SIZE
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    private val pokeApi: PokeApi,
    private val pokedexDatabase: PokedexDatabase
) : RemoteMediator<Int, PokemonListEntity>() {

    private val pokemonDao = pokedexDatabase.pokemonDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonListEntity>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }
            }

            val response = pokeApi.getPokemonList(PAGE_SIZE, currentPage * PAGE_SIZE)
            val endOfPaginationReached = response.results.isEmpty()

            val prevKey = if (currentPage == 0) null else currentPage - 1
            val nextKey = if (endOfPaginationReached) null else currentPage + 1

            pokedexDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    pokemonDao.clearPokemonList()
                    pokemonDao.clearRemoteKeys()
                }

                val pokemonEntries = response.results.map { entry ->
                    val pokemonName = entry.name.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                    }
                    val number = if (entry.url.endsWith("/")) {
                        entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                    } else {
                        entry.url.takeLastWhile { it.isDigit() }
                    }
                    val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"

                    // Preserve favorite status
                    val existingPokemon = pokemonDao.getPokemon(pokemonName).firstOrNull()
                    val isFavorite = existingPokemon?.isFavorite == true

                    PokemonListEntity(
                        pokemonName = pokemonName,
                        imageUrl = url,
                        number = number.toInt(),
                        isFavorite = isFavorite
                    )
                }
                pokemonDao.insertPokemonList(pokemonEntries)

                val keys = response.results.map { entry ->
                    val pokemonName = entry.name.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                    }
                    RemoteKeys(
                        pokemonName = pokemonName,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }
                pokemonDao.insertAllRemoteKeys(keys)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PokemonListEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { pokemon ->
                pokemonDao.getRemoteKeyForPokemon(pokemon.pokemonName)
            }
    }
}