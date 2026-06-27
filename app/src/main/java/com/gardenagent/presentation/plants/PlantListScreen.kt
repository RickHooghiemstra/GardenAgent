package com.gardenagent.presentation.plants

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.gardenagent.domain.model.Plant

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlantListScreen(
    onPlantClick: (Long) -> Unit,
    onAddPlant: () -> Unit,
    gardenId: Long? = null,
    viewModel: PlantListViewModel = hiltViewModel(),
) {
    val plants by viewModel.plants.collectAsStateWithLifecycle()
    var plantToDelete by remember { mutableStateOf<Plant?>(null) }

    LaunchedEffect(gardenId) { viewModel.setGardenFilter(gardenId) }

    plantToDelete?.let { plant ->
        AlertDialog(
            onDismissRequest = { plantToDelete = null },
            title = { Text("Delete Plant") },
            text = { Text("Delete \"${plant.commonName}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePlant(plant)
                    plantToDelete = null
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { plantToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Plants") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPlant) {
                Icon(Icons.Default.Add, contentDescription = "Add plant")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (gardenId != null) {
                FilterChip(
                    selected = true,
                    onClick = { /* navigating back clears it */ },
                    label = { Text("Filtered by garden") },
                    trailingIcon = { Icon(Icons.Default.Close, "Clear filter", Modifier.size(16.dp)) },
                    modifier = Modifier.padding(start = 12.dp, top = 8.dp),
                )
            }
            if (plants.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No plants yet.\nTap + to add your first plant.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(plants, key = { it.id }) { plant ->
                        PlantCard(
                            plant = plant,
                            onClick = { onPlantClick(plant.id) },
                            onLongPress = { plantToDelete = plant },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PlantCard(plant: Plant, onClick: () -> Unit, onLongPress: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().combinedClickable(onClick = onClick, onLongClick = onLongPress),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column {
            AsyncImage(
                model = plant.photoPath,
                contentDescription = plant.commonName,
                modifier = Modifier.fillMaxWidth().height(120.dp).clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
            )
            Column(Modifier.padding(8.dp)) {
                Text(plant.commonName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                plant.scientificName?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}
