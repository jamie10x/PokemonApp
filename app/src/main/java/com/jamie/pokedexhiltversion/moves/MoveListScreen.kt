package com.jamie.pokedexhiltversion.moves

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.jamie.pokedexhiltversion.data.local.models.MoveEntity
import java.util.Locale

@Composable
fun MoveListScreen(
    navController: NavController,
    viewModel: MoveListViewModel = hiltViewModel()
) {
    val moveList = viewModel.moveList.collectAsLazyPagingItems()

    Box(modifier = Modifier.fillMaxSize()) {
        if (moveList.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(moveList.itemCount) { index ->
                    moveList[index]?.let { move ->
                        MoveEntry(
                            entry = move,
                            onEntryClick = {
                                navController.navigate("move_detail_screen/${move.name}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MoveEntry(
    entry: MoveEntity,
    onEntryClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onEntryClick() }
            .padding(16.dp)
    ) {
        Text(
            text = entry.name
                .split("-")
                .joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.ROOT) else char.toString() } },
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}