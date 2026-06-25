package com.gardenagent.presentation.weather

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gardenagent.domain.model.WeatherData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather") },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading && state.weather == null -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                state.weather != null -> {
                    WeatherContent(state.weather!!, state.isLoading)
                }
                state.error != null -> {
                    Text(state.error!!, Modifier.align(Alignment.Center).padding(16.dp),
                        color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun WeatherContent(weather: WeatherData, isRefreshing: Boolean) {
    val updatedStr = remember(weather.recordedAt) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(weather.recordedAt))
    }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (isRefreshing) LinearProgressIndicator(Modifier.fillMaxWidth())
        Text("Last updated: $updatedStr",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        WeatherMetricCard("Temperature", "${weather.temperatureCelsius.toInt()}°C", "Current air temperature")
        WeatherMetricCard("Humidity", "${weather.humidity}%", "Relative air humidity")
        WeatherMetricCard("UV Index", weather.uvIndex.toString(), uvDescription(weather.uvIndex))
        WeatherMetricCard("Precipitation", "${weather.precipitationMm} mm", "Current hour precipitation")
        weather.soilTemperatureCelsius?.let {
            WeatherMetricCard("Soil Temperature", "${it.toInt()}°C", "0 cm depth soil temperature")
        }
    }
}

@Composable
private fun WeatherMetricCard(label: String, value: String, description: String) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(label, style = MaterialTheme.typography.titleMedium)
                Text(description, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(value, style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}

private fun uvDescription(uvIndex: Float) = when {
    uvIndex < 3 -> "Low — no protection needed"
    uvIndex < 6 -> "Moderate — some protection"
    uvIndex < 8 -> "High — protection required"
    uvIndex < 11 -> "Very high — extra protection"
    else -> "Extreme — avoid outdoor work"
}
