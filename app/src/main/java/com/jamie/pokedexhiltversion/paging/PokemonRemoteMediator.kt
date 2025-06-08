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
import com.jamie.pokedexhiltversion.data.remote.responses.Pokemon
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
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
            }

            val listResponse = pokeApi.getPokemonList(PAGE_SIZE, currentPage * PAGE_SIZE)
            val endOfPaginationReached = listResponse.results.isEmpty()

            // Fetch full details for each Pokémon in the list
            val pokemonDetails = listResponse.results.map {
                pokeApi.getPokemonInfo(it.name)
            }

            val prevKey = if (currentPage == 0) null else currentPage - 1
            val nextKey = if (endOfPaginationReached) null else currentPage + 1

            pokedexDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    pokemonDao.clearPokemonList()
                    pokemonDao.clearRemoteKeys()
                }

                val pokemonEntities = pokemonDetails.map { detail ->
                    val pokemonName = detail.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                    val existingPokemon = pokemonDao.getPokemon(pokemonName).firstOrNull()
                    PokemonListEntity(
                        pokemonName = pokemonName,
                        imageUrl = detail.sprites.front_default,
                        number = detail.id,
                        isFavorite = existingPokemon?.isFavorite == true,
                        pokemonInfo = detail,
                        types = detail.types.map { it.type.name },
                        hp = detail.stats.first { it.stat.name == "hp" }.base_stat,
                        attack = detail.stats.first { it.stat.name == "attack" }.base_stat,
                        defense = detail.stats.first { it.stat.name == "defense" }.base_stat,
                        specialAttack = detail.stats.first { it.stat.name == "special-attack" }.base_stat,
                        specialDefense = detail.stats.first { it.stat.name == "special-defense" }.base_stat,
                        speed = detail.stats.first { it.stat.name == "speed" }.base_stat
                    )
                }
                pokemonDao.insertPokemonList(pokemonEntities)

                val keys = listResponse.results.map { entry ->
                    val pokemonName = entry.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
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