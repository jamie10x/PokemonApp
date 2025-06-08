package com.jamie.pokedexhiltversion.moves

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jamie.pokedexhiltversion.data.remote.responses.move.MoveDetail
import com.jamie.pokedexhiltversion.repository.PokemonRepository
import com.jamie.pokedexhiltversion.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoveDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    var moveInfo = mutableStateOf<Resource<MoveDetail>>(Resource.Loading())
        private set

    fun loadMoveInfo(moveName: String) {
        viewModelScope.launch {
            moveInfo.value = Resource.Loading()
            moveInfo.value = repository.getMoveInfo(moveName)
        }
    }
}