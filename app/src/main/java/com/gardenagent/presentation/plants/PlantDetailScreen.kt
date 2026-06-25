package com.gardenagent.presentation.plants

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.gardenagent.domain.model.JournalEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlantDetailScreen(
    plantId: Long,
    onBack: () -> Unit,
    onAddJournalEntry: () -> Unit,
    onIdentify: () -> Unit,
    viewModel: PlantDetailViewModel = hiltViewModel(),
) {
    val plant by viewModel.plant.collectAsStateWithLifecycle()
    val entries by viewModel.entries.collectAsStateWithLifecycle()

    LaunchedEffect(plantId) { viewModel.loadPlant(plantId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(plant?.commonName ?: "Plant") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                actions = {
                    IconButton(onClick = onIdentify) { Icon(Icons.Default.Search, "Identify") }
                    IconButton(onClick = onAddJournalEntry) { Icon(Icons.Default.AddAPhoto, "Add journal entry") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            plant?.let { p ->
                item {
                    p.photoPath?.let { path ->
                        AsyncImage(
                            model = path,
                            contentDescription = p.commonName,
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            p.scientificName?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            p.family?.let { Text("Family: $it", style = MaterialTheme.typography.bodyMedium) }
                            p.locationInGarden?.let { Text("Location: $it", style = MaterialTheme.typography.bodyMedium) }
                            p.notes?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                        }
                    }
                }
                if (entries.isNotEmpty()) {
                    item { Text("Journal", style = MaterialTheme.typography.titleMedium) }
                    items(entries, key = { it.id }) { entry -> JournalEntryRow(entry) }
                }
            }
        }
    }
}

@Composable
private fun JournalEntryRow(entry: JournalEntry) {
    val dateStr = remember(entry.capturedAt) {
        SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(entry.capturedAt))
    }
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            entry.photoPath?.let {
                AsyncImage(model = it, contentDescription = null, modifier = Modifier.size(64.dp), contentScale = ContentScale.Crop)
            }
            Column {
                Text(dateStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                entry.notes?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            }
        }
    }
}
