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
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
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
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(text = item.label) },
                selected = currentRoute?.startsWith(item.route) == true,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // re-selecting the same item
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
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
        startDestination = BottomNavItem.Pokedex.route, // Start on the Pokédex tab
        modifier = modifier
    ) {
        // Pokedex Navigation Graph
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

        // Moves Navigation Graph
        navigation(
            startDestination = "move_list_screen",
            route = BottomNavItem.Moves.route
        ) {
            composable("move_list_screen") {
                MoveListScreen(navController = navController)
                // We will build this out in Part B
            }
            // Add composable for move_detail_screen here later
        }
    }
}