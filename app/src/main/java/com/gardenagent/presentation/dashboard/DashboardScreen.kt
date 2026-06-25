package com.gardenagent.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.gardenagent.domain.model.GardenAdvice
import com.gardenagent.domain.model.JournalEntry
import com.gardenagent.domain.model.WeatherData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    onNavigateToAdvice: () -> Unit,
    onNavigateToWeather: () -> Unit,
    onNavigateToJournalEntry: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Garden Agent") },
                actions = {
                    IconButton(onClick = viewModel::refreshAdvice) {
                        Icon(Icons.Default.AutoAwesome, "Refresh advice")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Weather card
            item {
                WeatherCard(
                    weather = state.weather,
                    isLoading = state.isLoadingWeather,
                    onClick = onNavigateToWeather,
                )
            }
            // Advice card
            item {
                AdviceCard(
                    advice = state.advice,
                    isLoading = state.isLoadingAdvice,
                    error = state.adviceError,
                    onRefresh = viewModel::refreshAdvice,
                )
            }
            // Recent journal entries
            if (state.recentEntries.isNotEmpty()) {
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Recent Journal", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = onNavigateToJournalEntry) { Text("Add entry") }
                    }
                }
                items(state.recentEntries, key = { it.id }) { entry ->
                    RecentEntryCard(entry)
                }
            }
        }
    }
}

@Composable
private fun WeatherCard(weather: WeatherData?, isLoading: Boolean, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Weather", style = MaterialTheme.typography.titleMedium)
            if (isLoading && weather == null) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            } else if (weather != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    WeatherChip("${weather.temperatureCelsius.toInt()}°C")
                    WeatherChip("${weather.humidity}% humidity")
                    WeatherChip("UV ${weather.uvIndex.toInt()}")
                }
                if (weather.precipitationMm > 0) {
                    Text("Rain: ${weather.precipitationMm} mm", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Text("Tap to load weather", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun WeatherChip(label: String) {
    SuggestionChip(onClick = {}, label = { Text(label) })
}

@Composable
private fun AdviceCard(
    advice: GardenAdvice?,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit,
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Garden Advice", style = MaterialTheme.typography.titleMedium)
                if (!isLoading) {
                    TextButton(onClick = onRefresh) {
                        Icon(Icons.Default.AutoAwesome, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Refresh")
                    }
                }
            }
            when {
                isLoading -> {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                    Text("Asking your garden advisor...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                error != null -> {
                    Text("Could not load advice: $error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium)
                }
                advice != null -> {
                    Text(advice.text, style = MaterialTheme.typography.bodyMedium)
                    val timeStr = remember(advice.generatedAt) {
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(advice.generatedAt))
                    }
                    Text("Generated at $timeStr",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                else -> {
                    Text("Tap Refresh to get personalized garden advice.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun RecentEntryCard(entry: JournalEntry) {
    val dateStr = remember(entry.capturedAt) {
        SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(entry.capturedAt))
    }
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            entry.photoPath?.let {
                AsyncImage(model = it, contentDescription = null,
                    modifier = Modifier.size(56.dp), contentScale = ContentScale.Crop)
            }
            Column {
                Text(dateStr, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                entry.notes?.let { Text(it, style = MaterialTheme.typography.bodyMedium, maxLines = 2) }
            }
        }
    }
}
