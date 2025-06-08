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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

enum class SortType {
    NUMBER, NAME, HP, ATTACK, DEFENSE
}

@OptIn(FlowPreview::class)
@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _sortType = MutableStateFlow(SortType.NUMBER)
    val sortType: StateFlow<SortType> = _sortType

    private val _selectedTypes = MutableStateFlow<List<String>>(emptyList())
    val selectedTypes: StateFlow<List<String>> = _selectedTypes

    @OptIn(ExperimentalCoroutinesApi::class)
    val pokemonList: Flow<PagingData<PokemonListEntity>> = combine(
        _searchQuery.debounce(500),
        _sortType,
        _selectedTypes
    ) { query, sort, types ->
        Triple(query, sort, types)
    }.flatMapLatest { (query, sort, types) ->
        repository.getPokemonList(query, sort, types)
    }.cachedIn(viewModelScope)


    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSortTypeChanged(sortType: SortType) {
        _sortType.value = sortType
    }

    fun onTypeSelected(type: String) {
        val currentTypes = _selectedTypes.value.toMutableList()
        if (currentTypes.contains(type)) {
            currentTypes.remove(type)
        } else {
            currentTypes.add(type)
        }
        _selectedTypes.value = currentTypes
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