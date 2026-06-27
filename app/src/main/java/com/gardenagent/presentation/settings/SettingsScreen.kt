package com.gardenagent.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gardenagent.BuildConfig

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNotificationSettings: () -> Unit = {},
    onGardenList: () -> Unit = {},
    onConnectEufy: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Eufy Account", style = MaterialTheme.typography.titleMedium)
            if (state.isLoggedIn) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Logged in as ${state.loggedInEmail ?: "Eufy account"}",
                            color = MaterialTheme.colorScheme.primary,
                        )
                        OutlinedButton(onClick = viewModel::logout, modifier = Modifier.fillMaxWidth()) {
                            Text("Log out")
                        }
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onConnectEufy,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Connect Eufy Cameras")
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Text("Gardens", style = MaterialTheme.typography.titleMedium)
            OutlinedButton(
                onClick = onGardenList,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Manage Gardens")
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Text("Notifications", style = MaterialTheme.typography.titleMedium)
            OutlinedButton(
                onClick = onNotificationSettings,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Notification Preferences")
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Version ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}
