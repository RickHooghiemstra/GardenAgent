package com.gardenagent.data.repository

import com.gardenagent.data.remote.claude.ClaudeApiService
import com.gardenagent.data.remote.claude.dto.ClaudeMessage
import com.gardenagent.data.remote.claude.dto.ClaudeRequest
import com.gardenagent.domain.model.GardenAdvice
import com.gardenagent.domain.model.Plant
import com.gardenagent.domain.model.SensorReading
import com.gardenagent.domain.model.WeatherData
import com.gardenagent.domain.repository.GardenAdviceRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named

private const val SYSTEM_PROMPT = """You are a professional garden advisor.
Analyze the provided garden data and give specific, actionable advice for today's garden care.
Keep your response under 300 words. Use bullet points (•) for tasks.
Focus on what needs to be done NOW based on the current conditions."""

class GardenAdviceRepositoryImpl @Inject constructor(
    private val api: ClaudeApiService,
    @Named("claude_api_key") private val apiKey: String,
) : GardenAdviceRepository {

    override suspend fun getAdvice(
        weather: WeatherData?,
        sensorReadings: List<SensorReading>,
        plants: List<Plant>,
    ): Result<GardenAdvice> = runCatching {
        val prompt = buildPrompt(weather, sensorReadings, plants)
        val response = api.sendMessage(
            apiKey = apiKey,
            request = ClaudeRequest(
                system = SYSTEM_PROMPT,
                messages = listOf(ClaudeMessage(role = "user", content = prompt))
            )
        )
        val text = response.content.firstOrNull { it.type == "text" }?.text
            ?: error("No text in Claude response")
        GardenAdvice(
            text = text,
            basedOnWeather = weather != null,
            basedOnSensors = sensorReadings.isNotEmpty(),
        )
    }

    internal fun buildPrompt(
        weather: WeatherData?,
        sensorReadings: List<SensorReading>,
        plants: List<Plant>,
    ): String = buildString {
        val now = SimpleDateFormat("EEEE, d MMMM yyyy HH:mm", Locale.ENGLISH).format(Date())
        appendLine("Date: $now")
        appendLine()

        if (weather != null) {
            appendLine("CURRENT WEATHER:")
            appendLine("• Temperature: ${weather.temperatureCelsius.toInt()}°C")
            appendLine("• Humidity: ${weather.humidity}%")
            appendLine("• UV Index: ${weather.uvIndex}")
            appendLine("• Precipitation: ${weather.precipitationMm} mm")
            weather.soilTemperatureCelsius?.let { appendLine("• Soil temperature: ${it.toInt()}°C") }
            appendLine()
        }

        if (sensorReadings.isNotEmpty()) {
            appendLine("SOIL SENSOR READINGS:")
            sensorReadings.groupBy { it.deviceAddress }.forEach { (_, readings) ->
                val latest = readings.maxByOrNull { it.recordedAt } ?: return@forEach
                appendLine("• ${latest.deviceName ?: "Sensor"}: moisture ${latest.moisturePercent}%, temp ${latest.temperatureCelsius}°C, light ${latest.lightLux} lux, fertility ${latest.fertilityMicroSiemens} µS/cm")
            }
            appendLine()
        }

        if (plants.isNotEmpty()) {
            appendLine("PLANTS IN GARDEN (${plants.size} total):")
            plants.take(10).forEach { plant ->
                append("• ${plant.commonName}")
                plant.scientificName?.let { append(" ($it)") }
                plant.locationInGarden?.let { append(" — location: $it") }
                appendLine()
            }
            if (plants.size > 10) appendLine("  ... and ${plants.size - 10} more")
            appendLine()
        }

        appendLine("Please provide specific garden care advice for today based on these conditions.")
    }
}
