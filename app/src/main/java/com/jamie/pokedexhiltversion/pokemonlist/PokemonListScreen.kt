package com.jamie.pokedexhiltversion.pokemonlist


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
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
import androidx.compose.ui.text.font.FontWeight
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
import com.jamie.pokedexhiltversion.util.parseTypeToColor
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    hint = "Search...",
                    modifier = Modifier.weight(1f),
                    text = searchQuery,
                    onTextChange = {
                        viewModel.onSearchQueryChanged(it)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { showBottomSheet = true }) {
                    Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter")
                }
            }

            PokemonList(navController = navController)
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                FilterSortContent(
                    viewModel = viewModel,
                    onApplyClicked = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FilterSortContent(
    viewModel: PokemonListViewModel,
    onApplyClicked: () -> Unit
) {
    val sortType by viewModel.sortType.collectAsState()
    val selectedTypes by viewModel.selectedTypes.collectAsState()

    val allTypes = listOf(
        "normal", "fire", "water", "electric", "grass", "ice", "fighting", "poison",
        "ground", "flying", "psychic", "bug", "rock", "ghost", "dragon", "dark", "steel", "fairy"
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sort by", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SortType.entries.forEach { type ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = type.ordinal, count = SortType.entries.size),
                    onClick = { viewModel.onSortTypeChanged(type) },
                    selected = type == sortType
                ) {
                    Text(type.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Filter by Type", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            allTypes.forEach { type ->
                FilterChip(
                    selected = selectedTypes.contains(type),
                    onClick = { viewModel.onTypeSelected(type) },
                    label = { Text(type.replaceFirstChar { it.uppercase() }) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = parseTypeToColor(com.jamie.pokedexhiltversion.data.remote.responses.Type(0, com.jamie.pokedexhiltversion.data.remote.responses.TypeX(type, ""))),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onApplyClicked, modifier = Modifier.fillMaxWidth()) {
            Text("Apply Filters")
        }
        Spacer(modifier = Modifier.height(16.dp))
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
                modifier = Modifier.padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 12.dp)
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
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    // Derived state to check if the scroll to top button should be shown
    val showScrollToTopButton by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex > 0
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Handle initial loading with shimmer
        if (pokemonListItems.loadState.refresh is LoadState.Loading) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(10) { ShimmerPokedexEntry() }
            }
        } else {
            // Main list
            LazyVerticalGrid(
                state = lazyGridState,
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    count = pokemonListItems.itemCount,
                    key = { index -> pokemonListItems.peek(index)?.pokemonName ?: index }
                ) { index ->
                    pokemonListItems[index]?.let { entry ->
                        PokedexEntry(
                            entry = entry,
                            navController = navController,
                            modifier = Modifier.animateItem(fadeInSpec = tween(500))
                        )
                    }
                }
            }
        }

        // Handle empty state AFTER loading is finished
        if (pokemonListItems.loadState.refresh is LoadState.NotLoading && pokemonListItems.itemCount == 0) {
            EmptyListMessage(modifier = Modifier.align(Center))
        }

        // Handle error states
        pokemonListItems.loadState.apply {
            val error = when {
                refresh is LoadState.Error -> refresh as LoadState.Error
                append is LoadState.Error -> append as LoadState.Error
                else -> null
            }
            error?.let {
                if (pokemonListItems.itemCount == 0) {
                    RetrySection(
                        error = it.error.localizedMessage ?: "An unknown error occurred",
                        modifier = Modifier.align(Center)
                    ) { pokemonListItems.retry() }
                }
            }
        }

        // Scroll to Top FAB
        AnimatedVisibility(
            visible = showScrollToTopButton,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            ) {
                Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Scroll to Top")
            }
        }
    }
}

@Composable
fun EmptyListMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Pokémon found",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Text(
            text = "Try adjusting your search or filters.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}


@Composable
fun ShimmerPokedexEntry() {
    Box(
        modifier = Modifier
            .shimmer()
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(Color.Gray.copy(alpha = 0.5f))
    ) {
        Column(
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.Gray.copy(alpha = 0.7f))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth(0.8f).height(20.dp).clip(RoundedCornerShape(10.dp)).background(Color.Gray.copy(alpha = 0.7f))
            )
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

    Box(
        contentAlignment = Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(Brush.verticalGradient(listOf(dominantColor, defaultDominantColor)))
            .clickable {
                navController.navigate("pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName.lowercase()}")
            }
    ) {
        if (entry.isFavorite) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorite",
                tint = Color.Red,
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
            )
        }

        Column(
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(entry.imageUrl)
                    .crossfade(true)
                    .listener(onSuccess = { _, result ->
                        viewModel.calcDominantColor(result.drawable.toBitmap()) { color ->
                            dominantColor = color
                        }
                    })
                    .build(),
                contentDescription = entry.pokemonName,
                modifier = Modifier.size(120.dp).align(CenterHorizontally)
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