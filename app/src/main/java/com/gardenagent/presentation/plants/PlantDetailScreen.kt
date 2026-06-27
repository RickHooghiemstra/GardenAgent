package com.gardenagent.presentation.plants

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
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

@OptIn(ExperimentalFoundationApi::class)
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

    var showDeletePlantConfirm by remember { mutableStateOf(false) }
    var journalEntryToDelete by remember { mutableStateOf<JournalEntry?>(null) }
    var expandedEntryId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(plantId) { viewModel.loadPlant(plantId) }

    if (showDeletePlantConfirm) {
        AlertDialog(
            onDismissRequest = { showDeletePlantConfirm = false },
            title = { Text("Delete Plant") },
            text = { Text("Delete \"${plant?.commonName}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePlant()
                    showDeletePlantConfirm = false
                    onBack()
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePlantConfirm = false }) { Text("Cancel") }
            }
        )
    }

    journalEntryToDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { journalEntryToDelete = null },
            title = { Text("Delete Entry") },
            text = { Text("Delete this journal entry? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteJournalEntry(entry)
                    journalEntryToDelete = null
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { journalEntryToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(plant?.commonName ?: "Plant") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                actions = {
                    IconButton(onClick = onIdentify) { Icon(Icons.Default.Search, "Identify") }
                    IconButton(onClick = onAddJournalEntry) { Icon(Icons.Default.AddAPhoto, "Add journal entry") }
                    IconButton(onClick = { showDeletePlantConfirm = true }) {
                        Icon(Icons.Default.Delete, "Delete plant")
                    }
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
                    items(entries, key = { it.id }) { entry ->
                        JournalEntryRow(
                            entry = entry,
                            isExpanded = expandedEntryId == entry.id,
                            onClick = { expandedEntryId = if (expandedEntryId == entry.id) null else entry.id },
                            onLongPress = { journalEntryToDelete = entry },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun JournalEntryRow(
    entry: JournalEntry,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
) {
    val dateStr = remember(entry.capturedAt) {
        SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(entry.capturedAt))
    }
    Card(
        Modifier.fillMaxWidth().combinedClickable(onClick = onClick, onLongClick = onLongPress)
    ) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            entry.photoPath?.let {
                AsyncImage(model = it, contentDescription = null, modifier = Modifier.size(64.dp), contentScale = ContentScale.Crop)
            }
            Column {
                Text(dateStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                entry.notes?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 3)
                }
            }
        }
    }
}
