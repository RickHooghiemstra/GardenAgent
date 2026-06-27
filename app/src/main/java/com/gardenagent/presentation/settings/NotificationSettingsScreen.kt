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
import com.gardenagent.data.local.preferences.NotificationPreferences

@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel(),
) {
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification Preferences") },
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Reminder Time", style = MaterialTheme.typography.titleMedium)
            ReminderTimePicker(
                hour = prefs.reminderHour,
                minute = prefs.reminderMinute,
                onHourChange = { h -> viewModel.update { it.copy(reminderHour = h) } },
                onMinuteChange = { m -> viewModel.update { it.copy(reminderMinute = m) } },
            )

            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Text("Reminders", style = MaterialTheme.typography.titleMedium)

            NotificationReminderRow(
                label = "Watering",
                enabled = prefs.wateringEnabled,
                daysInterval = prefs.wateringDaysInterval,
                onToggle = { viewModel.update { p -> p.copy(wateringEnabled = it) } },
                onDaysChange = { viewModel.update { p -> p.copy(wateringDaysInterval = it) } },
            )
            NotificationReminderRow(
                label = "Fertilizing",
                enabled = prefs.fertilizingEnabled,
                daysInterval = prefs.fertilizingDaysInterval,
                onToggle = { viewModel.update { p -> p.copy(fertilizingEnabled = it) } },
                onDaysChange = { viewModel.update { p -> p.copy(fertilizingDaysInterval = it) } },
            )
            NotificationReminderRow(
                label = "Mowing",
                enabled = prefs.mowingEnabled,
                daysInterval = prefs.mowingDaysInterval,
                onToggle = { viewModel.update { p -> p.copy(mowingEnabled = it) } },
                onDaysChange = { viewModel.update { p -> p.copy(mowingDaysInterval = it) } },
            )
            NotificationReminderRow(
                label = "Compost",
                enabled = prefs.compostEnabled,
                daysInterval = prefs.compostDaysInterval,
                onToggle = { viewModel.update { p -> p.copy(compostEnabled = it) } },
                onDaysChange = { viewModel.update { p -> p.copy(compostDaysInterval = it) } },
            )
            NotificationReminderRow(
                label = "Pesticides",
                enabled = prefs.pesticidesEnabled,
                daysInterval = prefs.pesticidesDaysInterval,
                onToggle = { viewModel.update { p -> p.copy(pesticidesEnabled = it) } },
                onDaysChange = { viewModel.update { p -> p.copy(pesticidesDaysInterval = it) } },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTimePicker(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
) {
    var showTimePicker by remember { mutableStateOf(false) }

    OutlinedButton(onClick = { showTimePicker = true }) {
        Text("Reminder at %02d:%02d".format(hour, minute))
    }

    if (showTimePicker) {
        BasicAlertDialog(onDismissRequest = { showTimePicker = false }) {
            Surface(shape = MaterialTheme.shapes.extraLarge, tonalElevation = 6.dp) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Select time",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 20.dp),
                    )
                    val timePickerState = rememberTimePickerState(initialHour = hour, initialMinute = minute)
                    TimePicker(state = timePickerState)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                        TextButton(onClick = {
                            onHourChange(timePickerState.hour)
                            onMinuteChange(timePickerState.minute)
                            showTimePicker = false
                        }) { Text("OK") }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationReminderRow(
    label: String,
    enabled: Boolean,
    daysInterval: Int,
    onToggle: (Boolean) -> Unit,
    onDaysChange: (Int) -> Unit,
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(label, style = MaterialTheme.typography.titleSmall)
                Switch(checked = enabled, onCheckedChange = onToggle)
            }
            if (enabled) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Every", style = MaterialTheme.typography.bodySmall)
                    IconButton(
                        onClick = { if (daysInterval > 1) onDaysChange(daysInterval - 1) },
                        modifier = Modifier.size(32.dp),
                    ) { Text("-") }
                    Text("$daysInterval days", style = MaterialTheme.typography.bodyMedium)
                    IconButton(
                        onClick = { onDaysChange(daysInterval + 1) },
                        modifier = Modifier.size(32.dp),
                    ) { Text("+") }
                }
            }
        }
    }
}
