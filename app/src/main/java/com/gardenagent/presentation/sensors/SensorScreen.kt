package com.gardenagent.presentation.sensors

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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

private enum class SensorBrand(val label: String, val protocol: String) {
    MI_FLORA("Xiaomi Mi Flora", "Bluetooth LE"),
    XIAOMI_HHCC("Xiaomi HHCC JCY10", "Bluetooth LE"),
    GOVEE("Govee", "WiFi / BLE"),
    INKBIRD("Inkbird IBS-TH", "Bluetooth LE"),
    SENSORPUSH("SensorPush", "Bluetooth LE"),
    AQARA("Aqara", "Zigbee / WiFi"),
    MANUAL("Manual / Other", "Enter readings manually"),
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SensorScreen(viewModel: SensorViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddManualDialog by remember { mutableStateOf(false) }

    val blePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        listOf(Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION)
    }
    val permissionState = rememberMultiplePermissionsState(blePermissions)

    if (showAddManualDialog) {
        AddManualSensorDialog(
            onDismiss = { showAddManualDialog = false },
            onAdd = { name, location ->
                viewModel.registerManualDevice(name, location)
                showAddManualDialog = false
            },
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Garden Sensors") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddManualDialog = true }) {
                Icon(Icons.Default.Add, "Add manual sensor")
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (!permissionState.allPermissionsGranted) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Bluetooth permissions required to scan for BLE soil sensors.")
                        Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                            Text("Grant Permissions")
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // Supported brands info
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Supported Sensors", style = MaterialTheme.typography.titleSmall)
                    SensorBrand.entries.forEach { brand ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(brand.label, style = MaterialTheme.typography.bodySmall)
                            Text(brand.protocol,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Text(
                        "BLE sensors are discovered via Scan below. WiFi/Zigbee sensors and manual entries can be added with the + button.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (permissionState.allPermissionsGranted) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("BLE Scan Results", style = MaterialTheme.typography.titleMedium)
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
                    Text("Scanning for BLE plant sensors…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                state.error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text("Error: $it", color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(12.dp))
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.discoveredDevices, key = { it.address }) { device ->
                    SensorDeviceCard(
                        device = device,
                        reading = state.readings[device.address],
                        registeredName = state.registeredDevices[device.address]?.deviceName,
                        registeredLocation = state.registeredDevices[device.address]?.locationDescription,
                        isReading = state.readingAddress == device.address,
                        onRead = { viewModel.readDevice(device.address, device.name) },
                        onRegister = { name, location, brand ->
                            viewModel.registerDevice(device.address, name, location, brand)
                        },
                    )
                }
                // Show registered manual sensors that don't have a BLE scan result
                items(
                    state.registeredDevices.values
                        .filter { reg -> state.discoveredDevices.none { it.address == reg.address } },
                    key = { "manual_${it.address}" },
                ) { reg ->
                    ManualSensorCard(
                        name = reg.deviceName,
                        location = reg.locationDescription,
                        reading = state.readings[reg.address],
                    )
                }
            }
        }
    }
}

@Composable
private fun AddManualSensorDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedBrand by remember { mutableStateOf(SensorBrand.MANUAL) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Sensor") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Sensor Brand", style = MaterialTheme.typography.labelMedium)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SensorBrand.entries.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            row.forEach { brand ->
                                FilterChip(
                                    selected = selectedBrand == brand,
                                    onClick = { selectedBrand = brand },
                                    label = { Text(brand.label, style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Sensor Name") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. Tomatoes sensor") },
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. Raised bed south") },
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAdd("${selectedBrand.label}: $name".trim().trimStart(':').trim(), location) },
                enabled = name.isNotBlank(),
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun SensorDeviceCard(
    device: SensorScanResult,
    reading: SensorReading?,
    registeredName: String?,
    registeredLocation: String?,
    isReading: Boolean,
    onRead: () -> Unit,
    onRegister: (String, String, String) -> Unit,
) {
    var showRegisterDialog by remember { mutableStateOf(false) }
    var registerName by remember(registeredName, device.name) { mutableStateOf(registeredName ?: device.name ?: "") }
    var registerLocation by remember(registeredLocation) { mutableStateOf(registeredLocation ?: "") }
    var registerBrand by remember { mutableStateOf(SensorBrand.MI_FLORA) }

    if (showRegisterDialog) {
        AlertDialog(
            onDismissRequest = { showRegisterDialog = false },
            title = { Text("Register Sensor") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Sensor Brand", style = MaterialTheme.typography.labelMedium)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        SensorBrand.entries.chunked(2).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                row.forEach { brand ->
                                    FilterChip(
                                        selected = registerBrand == brand,
                                        onClick = { registerBrand = brand },
                                        label = { Text(brand.label, style = MaterialTheme.typography.labelSmall) },
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                                if (row.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                    OutlinedTextField(
                        value = registerName,
                        onValueChange = { registerName = it },
                        label = { Text("Sensor Name") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = registerLocation,
                        onValueChange = { registerLocation = it },
                        label = { Text("Location") },
                        placeholder = { Text("e.g. Tomatoes bed") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onRegister(registerName, registerLocation, registerBrand.label)
                    showRegisterDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showRegisterDialog = false }) { Text("Cancel") }
            }
        )
    }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(registeredName ?: device.name ?: "BLE Sensor", style = MaterialTheme.typography.titleMedium)
                    Text(device.address, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (registeredLocation != null) {
                        AssistChip(
                            onClick = {},
                            label = { Text(registeredLocation, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.height(24.dp),
                        )
                    } else {
                        Text("Signal: ${device.rssi} dBm", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (isReading) {
                        CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        OutlinedButton(onClick = onRead) { Text("Read") }
                    }
                    TextButton(onClick = { showRegisterDialog = true }) {
                        Text(if (registeredName != null) "Edit" else "Register")
                    }
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
private fun ManualSensorCard(
    name: String,
    location: String?,
    reading: SensorReading?,
) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(name, style = MaterialTheme.typography.titleMedium)
                    location?.let {
                        AssistChip(
                            onClick = {},
                            label = { Text(it, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.height(24.dp),
                        )
                    }
                }
                AssistChip(onClick = {}, label = { Text("Manual") })
            }
            if (reading != null) {
                HorizontalDivider()
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    SensorValue("Moisture", "${reading.moisturePercent}%")
                    SensorValue("Temp", "${reading.temperatureCelsius}°C")
                    SensorValue("Light", "${reading.lightLux} lux")
                    SensorValue("Fertility", "${reading.fertilityMicroSiemens} µS")
                }
            } else {
                Text("No readings yet", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
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
