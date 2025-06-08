package com.jamie.pokedexhiltversion.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.jamie.pokedexhiltversion.data.local.models.MoveEntity
import com.jamie.pokedexhiltversion.data.local.models.PokemonListEntity
import com.jamie.pokedexhiltversion.data.local.models.RemoteKeys
import com.jamie.pokedexhiltversion.data.remote.responses.Pokemon
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    // For Pokemon List
    @RawQuery(observedEntities = [PokemonListEntity::class])
    fun getFilteredPokemonList(query: SupportSQLiteQuery): PagingSource<Int, PokemonListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonList(pokemonList: List<PokemonListEntity>)

    @Query("DELETE FROM pokemon_list")
    suspend fun clearPokemonList()

    @Query("SELECT * FROM pokemon_list WHERE pokemonName = :pokemonName")
    fun getPokemon(pokemonName: String): Flow<PokemonListEntity?>

    @Query("UPDATE pokemon_list SET pokemonInfo = :pokemonInfo WHERE pokemonName = :pokemonName")
    suspend fun updatePokemonInfo(pokemonName: String, pokemonInfo: Pokemon)

    @Query("UPDATE pokemon_list SET isFavorite = :isFavorite WHERE pokemonName = :pokemonName")
    suspend fun setFavorite(pokemonName: String, isFavorite: Boolean)

    // For Moves
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoveList(moves: List<MoveEntity>)

    @Query("SELECT * FROM moves ORDER BY name ASC")
    fun getMoveList(): PagingSource<Int, MoveEntity>

    @Query("DELETE FROM moves")
    suspend fun clearMoveList()


    // For Remote Keys
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE pokemonName = :pokemonName")
    suspend fun getRemoteKeyForPokemon(pokemonName: String): RemoteKeys?

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}