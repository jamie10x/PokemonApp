package com.jamie.pokedexhiltversion.pokemondetail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jamie.pokedexhiltversion.data.remote.responses.Pokemon
import com.jamie.pokedexhiltversion.data.remote.responses.evolution.Chain
import com.jamie.pokedexhiltversion.repository.PokemonRepository
import com.jamie.pokedexhiltversion.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    var pokemonInfo = mutableStateOf<Resource<Pokemon>>(Resource.Loading())
        private set

    var evolutionChain = mutableStateOf<Resource<List<Chain>>>(Resource.Loading())
        private set

    private val _isFavorite = MutableStateFlow<Boolean?>(null)
    val isFavorite: Flow<Boolean> = _isFavorite.filterNotNull()

    fun loadPokemonInfo(pokemonName: String) {
        val formattedName = pokemonName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

        viewModelScope.launch {
            // Collect the Flow from the repository
            repository.getPokemonInfo(formattedName).collect { result ->
                pokemonInfo.value = result
                // When we get a successful result (either from cache or network), load the evolution chain
                if (result is Resource.Success || (result is Resource.Loading && result.data != null)) {
                    loadEvolutionChain(result.data!!.name)
                }
            }
        }

        viewModelScope.launch {
            repository.getPokemonFromDb(formattedName).collect { pokemonEntity ->
                _isFavorite.value = pokemonEntity?.isFavorite
            }
        }
    }

    fun toggleFavorite(pokemonName: String) {
        val formattedName = pokemonName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        viewModelScope.launch {
            val currentStatus = _isFavorite.value
            if (currentStatus != null) {
                repository.setFavorite(formattedName, !currentStatus)
            }
        }
    }

    private fun loadEvolutionChain(pokemonName: String) {
        viewModelScope.launch {
            evolutionChain.value = Resource.Loading()
            when (val speciesResult = repository.getPokemonSpecies(pokemonName)) {
                is Resource.Success -> {
                    val evolutionChainUrl = speciesResult.data?.evolution_chain?.url
                    if (evolutionChainUrl != null) {
                        when (val chainResult = repository.getEvolutionChain(evolutionChainUrl)) {
                            is Resource.Success -> {
                                val evoChainList = mutableListOf<Chain>()
                                var currentChain = chainResult.data?.chain
                                while (currentChain != null) {
                                    evoChainList.add(currentChain)
                                    currentChain = currentChain.evolves_to.firstOrNull()
                                }
                                evolutionChain.value = Resource.Success(evoChainList)
                            }
                            is Resource.Error -> {
                                evolutionChain.value = Resource.Error(chainResult.message ?: "Failed to load evolution chain.")
                            }
                            is Resource.Loading -> { }
                        }
                    } else {
                        evolutionChain.value = Resource.Error("No evolution chain URL found.")
                    }
                }
                is Resource.Error -> {
                    evolutionChain.value = Resource.Error(speciesResult.message ?: "Failed to load species.")
                }
                is Resource.Loading -> { }
            }
        }
    }
}