package com.gardenagent.presentation.camera

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AddManualCameraScreen(
    onBack: () -> Unit,
    viewModel: AddManualCameraViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Camera") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Camera Name *") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Garden North") },
            )

            OutlinedTextField(
                value = state.rtspUrl,
                onValueChange = viewModel::onRtspUrlChange,
                label = { Text("RTSP URL *") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("rtsp://192.168.1.100/live0") },
            )

            OutlinedTextField(
                value = state.locationDescription,
                onValueChange = viewModel::onLocationChange,
                label = { Text("Location (optional)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Near the tomatoes") },
            )

            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving,
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                }
                Text("Add Camera")
            }
        }
    }
}
