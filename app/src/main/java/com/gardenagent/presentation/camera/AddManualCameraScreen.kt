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

private enum class CameraTemplate(
    val label: String,
    val urlHint: String,
    val helpText: String,
) {
    REOLINK(
        label = "Reolink",
        urlHint = "rtsp://admin:PASSWORD@192.168.x.x:554/h264Preview_01_main",
        helpText = "Use the admin password set in Reolink app. Main stream = _main, sub = _sub.",
    ),
    HIKVISION(
        label = "Hikvision",
        urlHint = "rtsp://admin:PASSWORD@192.168.x.x:554/Streaming/Channels/101",
        helpText = "Channel 101 = camera 1 main stream. Change last digit for sub-stream (102).",
    ),
    DAHUA(
        label = "Dahua",
        urlHint = "rtsp://admin:PASSWORD@192.168.x.x:554/cam/realmonitor?channel=1&subtype=0",
        helpText = "subtype=0 is main stream, subtype=1 is sub-stream.",
    ),
    WYZE(
        label = "Wyze (RTSP firmware)",
        urlHint = "rtsp://admin:PASSWORD@192.168.x.x:8554/unicast",
        helpText = "Requires Wyze RTSP firmware. Password set in Wyze app.",
    ),
    IP_WEBCAM(
        label = "IP Webcam (Android app)",
        urlHint = "rtsp://192.168.x.x:8080/h264_ulaw.sdp",
        helpText = "Start the IP Webcam app on an Android phone, then use the shown IP:port.",
    ),
    GENERIC(
        label = "Generic RTSP",
        urlHint = "rtsp://",
        helpText = "Any camera that supports RTSP streaming.",
    ),
}

@Composable
fun AddManualCameraScreen(
    onBack: () -> Unit,
    viewModel: AddManualCameraViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTemplate by remember { mutableStateOf<CameraTemplate?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saved) {
        if (state.saved) {
            snackbarHostState.showSnackbar("Camera added")
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Camera") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Camera Brand", style = MaterialTheme.typography.titleMedium)
            Text(
                "Eufy cameras are added automatically — log in via Settings. For all other cameras use RTSP below.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // Brand chip selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CameraTemplate.entries.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { template ->
                            FilterChip(
                                selected = selectedTemplate == template,
                                onClick = {
                                    selectedTemplate = template
                                    if (state.rtspUrl.isBlank()) {
                                        viewModel.onRtspUrlChange(template.urlHint)
                                    }
                                },
                                label = { Text(template.label) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }

            selectedTemplate?.let { t ->
                if (t.helpText.isNotEmpty()) {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Text(
                            t.helpText,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            HorizontalDivider()

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
                placeholder = { Text(selectedTemplate?.urlHint ?: "rtsp://192.168.1.100/live0") },
                singleLine = true,
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
