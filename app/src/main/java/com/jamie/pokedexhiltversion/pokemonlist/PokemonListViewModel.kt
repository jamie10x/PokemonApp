package com.jamie.pokedexhiltversion.pokemonlist

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.jamie.pokedexhiltversion.models.PokedexListEntry
import com.jamie.pokedexhiltversion.repository.PokemonRepository
import com.jamie.pokedexhiltversion.util.Constants.PAGE_SIZE
import com.jamie.pokedexhiltversion.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private var currentPage = 0

    var pokemonList = mutableStateOf<List< PokedexListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    init {
        loadPokemonPaginated()
    }

    fun loadPokemonPaginated() {
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.getPokemonList(PAGE_SIZE, currentPage * PAGE_SIZE)
            when (result) {
                is Resource.Success -> {
                    endReached.value = currentPage * PAGE_SIZE >= result.data!!.count
                    val pokedexEntries = result.data.results.mapIndexed { index, entry ->
                        val number = if (entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }
                        val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokedexListEntry(
                            entry.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                            url,
                            number.toInt()
                        )
                    }
                    currentPage++

                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value = pokemonList.value + pokedexEntries
                }
                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }
            }
        }
    }



    fun calcDominantColor(bitmap: Bitmap, onFinish: (Color) -> Unit) {
        val bmpCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmpCopy).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }

    /* Alternative approach
    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
    val bitmap = when (drawable) {
        is BitmapDrawable -> drawable.bitmap.copy(Bitmap.Config.ARGB_8888, true)
        else -> {
            val bmp = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bmp)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bmp
        }
    }

    Palette.from(bitmap).generate { palette ->
        palette?.dominantSwatch?.rgb?.let { colorValue ->
            onFinish(Color(colorValue))
        }
    }
}

     */
}