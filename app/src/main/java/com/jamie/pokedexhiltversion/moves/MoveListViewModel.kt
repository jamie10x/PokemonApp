package com.jamie.pokedexhiltversion.moves

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jamie.pokedexhiltversion.data.local.models.MoveEntity
import com.jamie.pokedexhiltversion.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoveListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    init {
        // Sync the moves when the ViewModel is first created
        viewModelScope.launch {
            repository.syncMoveList()
        }
    }

    val moveList: Flow<PagingData<MoveEntity>> = repository.getMoveList().cachedIn(viewModelScope)
}