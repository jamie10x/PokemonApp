package com.jamie.pokedexhiltversion.pokemonlist

import com.google.common.truth.Truth.assertThat
import com.jamie.pokedexhiltversion.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class PokemonListViewModelTest {

    private lateinit var viewModel: PokemonListViewModel
    private lateinit var mockRepository: PokemonRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mock()
        viewModel = PokemonListViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSearchQueryChanged updates searchQuery state`() = runTest {
        val newQuery = "Pikachu"
        viewModel.onSearchQueryChanged(newQuery)

        // Advance timers to allow debounce to pass
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.searchQuery.value).isEqualTo(newQuery)
    }

    @Test
    fun `onSortTypeChanged updates sortType state`() {
        val newSortType = SortType.NAME
        viewModel.onSortTypeChanged(newSortType)

        assertThat(viewModel.sortType.value).isEqualTo(newSortType)
    }

    @Test
    fun `onTypeSelected adds new type to list`() {
        val type = "fire"
        viewModel.onTypeSelected(type)

        assertThat(viewModel.selectedTypes.value).contains(type)
    }

    @Test
    fun `onTypeSelected removes existing type from list`() {
        val type = "water"
        // Add it first
        viewModel.onTypeSelected(type)
        assertThat(viewModel.selectedTypes.value).contains(type)

        // Then remove it
        viewModel.onTypeSelected(type)
        assertThat(viewModel.selectedTypes.value).doesNotContain(type)
    }
}