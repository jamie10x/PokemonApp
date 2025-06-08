package com.jamie.pokedexhiltversion.pokemonlist


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jamie.pokedexhiltversion.R
import com.jamie.pokedexhiltversion.data.local.models.PokemonListEntity

@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    // THE FIX: Use .collectAsState() for Flows and .value for State
    val searchQuery by viewModel.searchQuery.collectAsState()
    val listState = viewModel.listState.value

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                contentDescription = "Pokemon",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally)
            )
            SearchBar(
                hint = "Search...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = searchQuery,
                onTextChange = {
                    viewModel.onSearchQueryChanged(it)
                }
            )

            ListStateToggle(
                currentListState = listState,
                onStateChanged = { viewModel.setListState(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            PokemonList(navController = navController)
        }
    }
}

@Composable
fun ListStateToggle(
    currentListState: ListScreenState,
    onStateChanged: (ListScreenState) -> Unit,
    modifier: Modifier = Modifier
) {
    SegmentedButton(
        modifier = modifier,
        segments = listOf("All", "Favorites"),
        selectedIndex = currentListState.ordinal,
        onSegmentSelected = { index ->
            onStateChanged(ListScreenState.entries[index])
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedButton(
    segments: List<String>,
    selectedIndex: Int,
    onSegmentSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        segments.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = segments.size
                ),
                onClick = { onSegmentSelected(index) },
                selected = index == selectedIndex,
                modifier = Modifier.weight(1f)
            ) {
                Text(label)
            }
        }
    }
}


@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    text: String,
    onTextChange: (String) -> Unit
) {
    var isHintDisplayed by remember { mutableStateOf(hint.isNotEmpty()) }

    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged { isHintDisplayed = !it.isFocused && text.isEmpty() }
        )

        if (isHintDisplayed && text.isEmpty()) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier
                    .padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 12.dp)
            )
        }
    }
}


@Composable
fun PokemonList(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val pokemonListItems: LazyPagingItems<PokemonListEntity> =
        viewModel.pokemonList.collectAsLazyPagingItems()

    Box(modifier = Modifier.fillMaxSize()) {
        if (pokemonListItems.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Center)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(pokemonListItems.itemCount) { index ->
                    pokemonListItems[index]?.let {
                        PokedexEntry(
                            entry = it,
                            navController = navController
                        )
                    }
                }

                // Handle loading state for appending new pages
                item {
                    if (pokemonListItems.loadState.append is LoadState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp).wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }

        // Handle error states
        pokemonListItems.loadState.apply {
            val error = when {
                refresh is LoadState.Error -> refresh as LoadState.Error
                append is LoadState.Error -> append as LoadState.Error
                else -> null
            }
            error?.let {
                RetrySection(
                    error = it.error.localizedMessage ?: "An unknown error occurred",
                    modifier = Modifier.align(Center)
                ) {
                    pokemonListItems.retry()
                }
            }
        }
    }
}

@Composable
fun PokedexEntry(
    entry: PokemonListEntity,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val defaultDominantColor = MaterialTheme.colorScheme.surface
    var dominantColor by remember { mutableStateOf(defaultDominantColor) }
    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(dominantColor, defaultDominantColor)
                )
            )
            .clickable {
                navController.navigate(
                    "pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName.lowercase()}"
                )
            }
    ) {
        if(entry.isFavorite) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorite",
                tint = Color.Red,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }

        Column(
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.scale(0.5f)
                )
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(entry.imageUrl)
                    .crossfade(true)
                    .listener(
                        onStart = { isLoading = true },
                        onSuccess = { _, result ->
                            viewModel.calcDominantColor(result.drawable.toBitmap()) { color ->
                                dominantColor = color
                            }
                            isLoading = false
                        },
                        onError = { _, _ -> isLoading = false }
                    )
                    .build(),
                contentDescription = entry.pokemonName,
                modifier = Modifier
                    .size(120.dp)
                    .align(CenterHorizontally)
            )
            Text(
                text = entry.pokemonName,
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun RetrySection(
    error: String,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(error, color = Color.Red, fontSize = 18.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }
}