package com.gardenagent.presentation.sensors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gardenagent.domain.model.SensorReading
import com.gardenagent.domain.provider.SensorScanResult
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import android.Manifest
import android.os.Build

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SensorScreen(viewModel: SensorViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val blePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        listOf(Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION)
    }
    val permissionState = rememberMultiplePermissionsState(blePermissions)

    Scaffold(topBar = { TopAppBar(title = { Text("Soil Sensors") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (!permissionState.allPermissionsGranted) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Bluetooth permissions required to scan for soil sensors.")
                        Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                            Text("Grant Permissions")
                        }
                    }
                }
            } else {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Mi Flora Sensors", style = MaterialTheme.typography.titleMedium)
                    if (state.isScanning) {
                        Button(onClick = viewModel::stopScan) { Text("Stop") }
                    } else {
                        Button(onClick = viewModel::startScan) {
                            Icon(Icons.Default.BluetoothSearching, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Scan")
                        }
                    }
                }
                if (state.isScanning) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                    Text("Scanning for Mi Flora sensors…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                state.error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text("Error: $it", color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(12.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.discoveredDevices, key = { it.address }) { device ->
                        SensorDeviceCard(
                            device = device,
                            reading = state.readings[device.address],
                            isReading = state.readingAddress == device.address,
                            onRead = { viewModel.readDevice(device.address, device.name) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SensorDeviceCard(
    device: SensorScanResult,
    reading: SensorReading?,
    isReading: Boolean,
    onRead: () -> Unit,
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(device.name ?: "Mi Flora", style = MaterialTheme.typography.titleMedium)
                    Text(device.address, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Signal: ${device.rssi} dBm", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (isReading) {
                    CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    OutlinedButton(onClick = onRead) { Text("Read") }
                }
            }
            reading?.let { r ->
                HorizontalDivider()
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    SensorValue("Moisture", "${r.moisturePercent}%")
                    SensorValue("Temp", "${r.temperatureCelsius}°C")
                    SensorValue("Light", "${r.lightLux} lux")
                    SensorValue("Fertility", "${r.fertilityMicroSiemens} µS")
                }
            }
        }
    }
}

@Composable
private fun SensorValue(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
