package com.jamie.pokedexhiltversion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.jamie.pokedexhiltversion.moves.MoveDetailScreen
import com.jamie.pokedexhiltversion.moves.MoveListScreen
import com.jamie.pokedexhiltversion.pokemondetail.PokemonDetailScreen
import com.jamie.pokedexhiltversion.pokemonlist.BottomNavItem
import com.jamie.pokedexhiltversion.pokemonlist.PokemonListScreen
import com.jamie.pokedexhiltversion.ui.theme.PokedexHiltVersionTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexHiltVersionTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { AppBottomNavigationBar(navController = navController) }
                ) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Pokedex,
        BottomNavItem.Moves
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Hide bottom bar on detail screens
    val bottomBarDestination = items.any { it.route == currentDestination?.parent?.route }
    if (!bottomBarDestination) {
        return
    }

    NavigationBar {
        items.forEach { item ->
            val isSelected = currentDestination?.parent?.route == item.route
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(text = item.label) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Pokedex.route,
        modifier = modifier
    ) {
        navigation(
            startDestination = "pokemon_list_screen",
            route = BottomNavItem.Pokedex.route
        ) {
            composable("pokemon_list_screen") {
                PokemonListScreen(navController = navController)
            }
            composable(
                "pokemon_detail_screen/{dominantColor}/{pokemonName}",
                arguments = listOf(
                    navArgument("dominantColor") { type = NavType.IntType },
                    navArgument("pokemonName") { type = NavType.StringType }
                ),
                enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) }
            ) { backStackEntry ->
                val dominantColor = remember {
                    val color = backStackEntry.arguments?.getInt("dominantColor")
                    color?.let { Color(it) } ?: Color.White
                }
                val pokemonName = remember {
                    backStackEntry.arguments?.getString("pokemonName")
                }
                PokemonDetailScreen(
                    dominantColor = dominantColor,
                    pokemonName = pokemonName?.lowercase(Locale.ROOT) ?: "",
                    navController = navController
                )
            }
        }

        navigation(
            startDestination = "move_list_screen",
            route = BottomNavItem.Moves.route
        ) {
            composable("move_list_screen") {
                MoveListScreen(navController = navController)
            }
            composable(
                "move_detail_screen/{moveName}",
                arguments = listOf(
                    navArgument("moveName") { type = NavType.StringType }
                ),
                enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) }
            ) { backStackEntry ->
                val moveName = remember {
                    backStackEntry.arguments?.getString("moveName") ?: ""
                }
                MoveDetailScreen(
                    moveName = moveName,
                    navController = navController
                )
            }
        }
    }
}