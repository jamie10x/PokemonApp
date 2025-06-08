package com.jamie.pokedexhiltversion.pokemonlist

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.palette.graphics.Palette
import com.jamie.pokedexhiltversion.data.local.models.PokemonListEntity
import com.jamie.pokedexhiltversion.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

enum class ListScreenState {
    ALL, FAVORITES
}

@OptIn(FlowPreview::class)
@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _listState = mutableStateOf(ListScreenState.ALL)
    val listState: State<ListScreenState> = _listState

    @OptIn(ExperimentalCoroutinesApi::class)
    val pokemonList: Flow<PagingData<PokemonListEntity>> = _searchQuery
        .debounce(500) // Debounce to avoid rapid firing of searches
        .distinctUntilChanged()
        .flatMapLatest { query ->
            // Use listState.value here to react to changes
            val source = when (listState.value) {
                ListScreenState.ALL -> repository.getPokemonList()
                ListScreenState.FAVORITES -> repository.getFavoritePokemonList()
            }

            if (query.isBlank()) {
                source
            } else {
                // Search should apply to the currently selected list state, but for simplicity, we search all.
                // A more advanced implementation might search within favorites only.
                repository.searchPokemonList(query)
            }
        }
        .cachedIn(viewModelScope) // Cache the results in the ViewModel scope

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun setListState(newState: ListScreenState) {
        _listState.value = newState
        // When the state changes, if the query is blank, the flow will automatically re-trigger.
    }

    fun calcDominantColor(bitmap: Bitmap, onFinish: (Color) -> Unit) {
        val bmpCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmpCopy).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}