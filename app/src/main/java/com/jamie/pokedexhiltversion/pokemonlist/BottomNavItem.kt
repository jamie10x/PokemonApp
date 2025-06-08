package com.jamie.pokedexhiltversion.pokemonlist

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Games
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Pokedex : BottomNavItem("pokedex_section", Icons.Default.CatchingPokemon, "Pokédex")
    object Moves : BottomNavItem("moves_section", Icons.Default.Games, "Moves")
}