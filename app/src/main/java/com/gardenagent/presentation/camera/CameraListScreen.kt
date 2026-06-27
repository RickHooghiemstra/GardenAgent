package com.gardenagent.presentation.camera

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Doorbell
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CameraListScreen(
    onCameraClick: (String) -> Unit,
    onSettings: () -> Unit,
    onAddManualCamera: () -> Unit = {},
    viewModel: CameraListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var cameraToDelete by remember { mutableStateOf<CameraListItem?>(null) }

    cameraToDelete?.let { camera ->
        AlertDialog(
            onDismissRequest = { cameraToDelete = null },
            title = { Text("Delete Camera") },
            text = { Text("Delete \"${camera.name}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteManualCamera(camera)
                    cameraToDelete = null
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { cameraToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cameras") },
                actions = {
                    IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, "Settings") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddManualCamera) {
                Icon(Icons.Default.Add, "Add camera")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
            state.error?.let {
                Text("Error: $it\nCheck Eufy login in Settings.",
                    Modifier.padding(16.dp), color = MaterialTheme.colorScheme.error)
            }
            if (state.cameras.isEmpty() && !state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No cameras found.", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(8.dp))
                        Text("Log in to Eufy in Settings or add a manual RTSP camera.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onSettings) { Text("Go to Settings") }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.cameras, key = { it.id }) { camera ->
                        CameraListItemCard(
                            camera = camera,
                            onClick = { onCameraClick(camera.id) },
                            onLongPress = { cameraToDelete = camera },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CameraListItemCard(
    camera: CameraListItem,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = if (camera.isManual) onLongPress else null,
            )
    ) {
        Row(
            Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (camera.isDoorbell) Icons.Default.Doorbell else Icons.Default.Videocam,
                    contentDescription = null,
                    tint = if (camera.isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp),
                )
                Column {
                    Text(camera.name, style = MaterialTheme.typography.titleMedium)
                    camera.model?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    camera.location?.let {
                        AssistChip(
                            onClick = {},
                            label = { Text(it, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.height(24.dp),
                        )
                    }
                    if (camera.isManual) {
                        Text("Manual RTSP", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary)
                    }
                }
            }
            Badge(
                containerColor = if (camera.isOnline) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    if (camera.isManual) "RTSP" else if (camera.isOnline) "Online" else "Offline",
                    color = if (camera.isOnline) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
