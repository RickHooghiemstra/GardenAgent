package com.gardenagent.presentation.camera

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun CameraViewScreen(
    deviceSn: String,
    onBack: () -> Unit,
    viewModel: CameraViewViewModel = hiltViewModel(),
) {
    val rtspUrl by viewModel.rtspUrl.collectAsStateWithLifecycle()

    LaunchedEffect(deviceSn) { viewModel.loadCamera(deviceSn) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(viewModel.cameraName.collectAsStateWithLifecycle().value ?: "Camera") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                rtspUrl == null -> {
                    Column(Modifier.align(Alignment.Center)) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(8.dp))
                        Text("Connecting to camera…")
                    }
                }
                rtspUrl == "" -> {
                    Column(Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Camera not reachable", style = MaterialTheme.typography.titleMedium)
                        Text("Make sure you are connected to your home WiFi network.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                else -> {
                    RtspPlayer(rtspUrl = rtspUrl!!)
                }
            }
        }
    }
}

@Composable
private fun RtspPlayer(rtspUrl: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(rtspUrl)))
            prepare()
            playWhenReady = true
        }
    }
    DisposableEffect(exoPlayer) {
        onDispose { exoPlayer.release() }
    }
    AndroidView(
        factory = { PlayerView(it).apply { player = exoPlayer } },
        modifier = Modifier.fillMaxSize(),
    )
}
