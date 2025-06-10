package com.jamie.pokedexhiltversion.moves

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jamie.pokedexhiltversion.data.remote.responses.move.MoveDetail
import com.jamie.pokedexhiltversion.util.Resource
import com.jamie.pokedexhiltversion.util.parseTypeToColor
import java.util.Locale

@Composable
fun MoveDetailScreen(
    moveName: String,
    navController: NavController,
    viewModel: MoveDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = moveName) {
        viewModel.loadMoveInfo(moveName)
    }

    val moveInfo by remember { viewModel.moveInfo }
    val dominantColor = if (moveInfo is Resource.Success) {
        parseTypeToColor((moveInfo.data!!.type))
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(
        color = dominantColor,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Draw content first, so it's in the background
            when (val result = moveInfo) {
                is Resource.Success -> {
                    MoveDetailContent(move = result.data!!)
                }
                is Resource.Error -> {
                    Text(
                        text = result.message ?: "An error occurred",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // Draw IconButton last, so it's on top and clickable
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun MoveDetailContent(move: MoveDetail) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = move.name
                .split("-")
                .joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.ROOT) else char.toString() } },
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Type Badge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .background(parseTypeToColor(move.type))
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(
                text = move.type.name.uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MoveStatItem(label = "Power", value = move.power?.toString() ?: "--")
            MoveStatItem(label = "Accuracy", value = move.accuracy?.toString() ?: "--")
            MoveStatItem(label = "PP", value = move.pp.toString())
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Description
        val englishFlavorText = move.flavorTextEntries.find { it.language.name == "en" }?.flavorText
        if (englishFlavorText != null) {
            Text(
                text = englishFlavorText.replace("\n", " "),
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun MoveStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 18.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            fontSize = 22.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}