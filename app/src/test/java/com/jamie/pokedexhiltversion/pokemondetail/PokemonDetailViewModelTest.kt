package com.jamie.pokedexhiltversion.pokemondetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.jamie.pokedexhiltversion.data.local.models.PokemonListEntity
import com.jamie.pokedexhiltversion.data.remote.responses.Pokemon
import com.jamie.pokedexhiltversion.repository.PokemonRepository
import com.jamie.pokedexhiltversion.util.MainCoroutineRule
import com.jamie.pokedexhiltversion.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Locale

@ExperimentalCoroutinesApi
class PokemonDetailViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: PokemonDetailViewModel
    private lateinit var mockRepository: PokemonRepository

    @Before
    fun setUp() {
        mockRepository = mock()
        viewModel = PokemonDetailViewModel(mockRepository)
    }

    @Test
    fun `loadPokemonInfo updates pokemonInfo state to Success`() = runTest {
        val pokemonName = "bulbasaur"
        val formattedName = pokemonName.replaceFirstChar { it.titlecase(Locale.ROOT) }
        val mockPokemon = mock<Pokemon> {
            on { name } doReturn pokemonName
        }
        val successResource = Resource.Success(mockPokemon)

        // ARRANGE: Use the correctly formatted name for mocking
        whenever(mockRepository.getPokemonInfo(formattedName)).doReturn(flowOf(successResource))
        whenever(mockRepository.getPokemonFromDb(formattedName)).doReturn(flowOf(null))
        whenever(mockRepository.getPokemonSpecies(pokemonName)).doReturn(Resource.Error("Not needed"))
        whenever(mockRepository.getEvolutionChain(any())).doReturn(Resource.Error("Not needed"))

        // ACT
        viewModel.loadPokemonInfo(pokemonName)
        advanceUntilIdle()

        // ASSERT
        val result = viewModel.pokemonInfo.value
        assertThat(result).isInstanceOf(Resource.Success::class.java)
        assertThat(result.data).isEqualTo(mockPokemon)
    }

    @Test
    fun `loadPokemonInfo handles network error`() = runTest {
        val pokemonName = "charmander"
        val formattedName = pokemonName.replaceFirstChar { it.titlecase(Locale.ROOT) }
        val errorResource = Resource.Error<Pokemon>("Network error", null)

        // ARRANGE
        whenever(mockRepository.getPokemonInfo(formattedName)).doReturn(flowOf(errorResource))
        whenever(mockRepository.getPokemonFromDb(formattedName)).doReturn(flowOf(null))
        whenever(mockRepository.getPokemonSpecies(pokemonName)).doReturn(Resource.Error("Not needed"))
        whenever(mockRepository.getEvolutionChain(any())).doReturn(Resource.Error("Not needed"))

        // ACT
        viewModel.loadPokemonInfo(pokemonName)
        advanceUntilIdle()

        // ASSERT
        val result = viewModel.pokemonInfo.value
        assertThat(result).isInstanceOf(Resource.Error::class.java)
        assertThat(result.message).isEqualTo("Network error")
    }

    @Test
    fun `toggleFavorite correctly toggles from not favorite to favorite`() = runTest {
        val pokemonName = "Squirtle"
        val lowercaseName = pokemonName.lowercase(Locale.ROOT)
        val mockEntity = mock<PokemonListEntity> {
            on { isFavorite } doReturn false
        }
        val mockPokemon = mock<Pokemon> {
            on { name } doReturn lowercaseName
        }

        // ARRANGE
        whenever(mockRepository.getPokemonFromDb(pokemonName)).doReturn(flowOf(mockEntity))
        whenever(mockRepository.getPokemonInfo(pokemonName)).doReturn(flowOf(Resource.Success(mockPokemon)))
        whenever(mockRepository.getPokemonSpecies(any())).doReturn(Resource.Error("Not needed"))
        whenever(mockRepository.getEvolutionChain(any())).doReturn(Resource.Error("Not needed"))

        // ACT
        viewModel.loadPokemonInfo(lowercaseName)
        advanceUntilIdle()
        viewModel.toggleFavorite(lowercaseName)
        advanceUntilIdle()

        // ASSERT
        verify(mockRepository).setFavorite(pokemonName, true)
    }

    @Test
    fun `toggleFavorite correctly toggles from favorite to not favorite`() = runTest {
        val pokemonName = "Pikachu"
        val lowercaseName = pokemonName.lowercase(Locale.ROOT)
        val mockEntity = mock<PokemonListEntity> {
            on { isFavorite } doReturn true
        }
        val mockPokemon = mock<Pokemon> {
            on { name } doReturn lowercaseName
        }

        // ARRANGE
        whenever(mockRepository.getPokemonFromDb(pokemonName)).doReturn(flowOf(mockEntity))
        whenever(mockRepository.getPokemonInfo(pokemonName)).doReturn(flowOf(Resource.Success(mockPokemon)))
        whenever(mockRepository.getPokemonSpecies(any())).doReturn(Resource.Error("Not needed"))
        // THE FIX IS HERE: Changed 'mock' to 'mockRepository'
        whenever(mockRepository.getEvolutionChain(any())).doReturn(Resource.Error("Not needed"))

        // ACT
        viewModel.loadPokemonInfo(lowercaseName)
        advanceUntilIdle()
        viewModel.toggleFavorite(lowercaseName)
        advanceUntilIdle()

        // ASSERT
        verify(mockRepository).setFavorite(pokemonName, false)
    }
}