package com.jamie.pokedexhiltversion.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jamie.pokedexhiltversion.data.local.models.PokemonListEntity
import com.jamie.pokedexhiltversion.data.local.models.RemoteKeys
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    // For Pokemon List
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonList(pokemonList: List<PokemonListEntity>)

    @Query("SELECT * FROM pokemon_list ORDER BY number ASC")
    fun getPokemonList(): PagingSource<Int, PokemonListEntity>

    @Query("SELECT * FROM pokemon_list WHERE isFavorite = 1 ORDER BY number ASC")
    fun getFavoritePokemonList(): PagingSource<Int, PokemonListEntity>

    @Query("SELECT * FROM pokemon_list WHERE pokemonName LIKE :query ORDER BY number ASC")
    fun searchPokemonList(query: String): PagingSource<Int, PokemonListEntity>

    @Query("DELETE FROM pokemon_list")
    suspend fun clearPokemonList()

    @Query("SELECT * FROM pokemon_list WHERE pokemonName = :pokemonName")
    fun getPokemon(pokemonName: String): Flow<PokemonListEntity?>

    @Query("UPDATE pokemon_list SET isFavorite = :isFavorite WHERE pokemonName = :pokemonName")
    suspend fun setFavorite(pokemonName: String, isFavorite: Boolean)


    // For Remote Keys
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE pokemonName = :pokemonName")
    suspend fun getRemoteKeyForPokemon(pokemonName: String): RemoteKeys?

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}