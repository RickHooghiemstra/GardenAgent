package com.gardenagent.presentation.identify

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gardenagent.domain.model.PlantIdentification
import com.gardenagent.presentation.common.PhotoCapture
import kotlin.math.roundToInt

@Composable
fun IdentifyPlantScreen(
    onBack: () -> Unit,
    onPlantAdded: () -> Unit = {},
    viewModel: IdentifyPlantViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.addedPlantName) {
        state.addedPlantName?.let { name ->
            snackbarHostState.showSnackbar("\"$name\" added to My Garden")
            viewModel.clearAddedPlant()
            onPlantAdded()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Identify Plant") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                PhotoCapture(
                    photoPath = state.photoPath,
                    onPhotoTaken = viewModel::onPhotoTaken,
                )
            }
            item {
                Button(
                    onClick = viewModel::identify,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.photoPath != null && !state.isLoading,
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Identifying…")
                    } else {
                        Text("Identify Plant")
                    }
                }
            }
            state.error?.let {
                item {
                    Text("Error: $it", color = MaterialTheme.colorScheme.error)
                }
            }
            if (state.results.isNotEmpty()) {
                item { Text("Results", style = MaterialTheme.typography.titleMedium) }
                items(state.results, key = { it.scientificName }) { result ->
                    IdentificationResultCard(
                        result = result,
                        onAddToGarden = { viewModel.addToGarden(result) },
                    )
                }
            }
        }
    }
}

@Composable
private fun IdentificationResultCard(
    result: PlantIdentification,
    onAddToGarden: () -> Unit,
) {
    val pct = (result.confidence * 100).roundToInt()
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(result.commonName, style = MaterialTheme.typography.titleMedium)
                    Text(result.scientificName, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (result.family.isNotBlank()) {
                        Text("Family: ${result.family}", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Text("$pct%", style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary)
            }
            LinearProgressIndicator(
                progress = { result.confidence },
                modifier = Modifier.fillMaxWidth(),
                color = when {
                    pct >= 70 -> MaterialTheme.colorScheme.primary
                    pct >= 40 -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.outline
                }
            )
            OutlinedButton(
                onClick = onAddToGarden,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Add to My Garden")
            }
        }
    }
}
