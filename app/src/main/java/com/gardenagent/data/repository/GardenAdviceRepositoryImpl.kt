package com.gardenagent.data.repository

import com.gardenagent.data.remote.claude.ClaudeApiService
import com.gardenagent.data.remote.claude.dto.ClaudeMessage
import com.gardenagent.data.remote.claude.dto.ClaudeRequest
import com.gardenagent.domain.model.ForecastDay
import com.gardenagent.domain.model.GardenAdvice
import com.gardenagent.domain.model.JournalEntry
import com.gardenagent.domain.model.MaintenanceTask
import com.gardenagent.domain.model.Plant
import com.gardenagent.domain.model.SensorReading
import com.gardenagent.domain.model.WeatherData
import com.gardenagent.domain.repository.GardenAdviceRepository
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named

private const val SYSTEM_PROMPT = """You are a professional garden advisor.
Analyze the provided garden data and give specific, actionable advice for today's garden care.
Keep your response under 300 words. Use bullet points (•) for tasks.
Focus on what needs to be done NOW based on the current conditions."""

private const val JOURNAL_SYSTEM_PROMPT = """You are a garden journal assistant.
Write a brief, warm daily journal entry for the gardener based on the provided conditions.
Keep it under 200 words, personal and observational in tone.
Mention what is happening in the garden today based on the weather and sensor data."""

private const val SCHEDULE_SYSTEM_PROMPT = """You are a garden advisor generating a 7-day maintenance schedule.
Output ONLY a JSON array with no prose, no markdown except the array itself. Format exactly:
[{"task":"description","date":"YYYY-MM-DD","time":"HH:mm","plant":"plant name or null","reason":"one sentence"}]
Rules:
- Schedule 3-7 tasks spread across the forecast window
- Skip watering on rain days (precipitation > 3 mm)
- Schedule watering early morning (07:00)
- Fertilise only if fertility readings are below 200 µS/cm
- Reference specific plant names when relevant
- Use realistic, actionable task descriptions"""

@Serializable
private data class RawScheduleTask(
    val task: String,
    val date: String,
    val time: String,
    val plant: String? = null,
    val reason: String = "",
)

private val scheduleJson = Json { ignoreUnknownKeys = true; isLenient = true }

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

    override suspend fun getDailyJournalEntry(
        weather: WeatherData?,
        sensorReadings: List<SensorReading>,
        plants: List<Plant>,
    ): Result<String> = runCatching {
        val prompt = buildPrompt(weather, sensorReadings, plants)
        val response = api.sendMessage(
            apiKey = apiKey,
            request = ClaudeRequest(
                system = JOURNAL_SYSTEM_PROMPT,
                messages = listOf(ClaudeMessage(role = "user", content = prompt))
            )
        )
        response.content.firstOrNull { it.type == "text" }?.text
            ?: error("No text in Claude response")
    }

    override suspend fun getMaintenanceSchedule(
        forecast: List<ForecastDay>,
        recentSensorHistory: List<SensorReading>,
        recentJournalEntries: List<JournalEntry>,
        plants: List<Plant>,
    ): Result<List<MaintenanceTask>> = runCatching {
        val prompt = buildSchedulePrompt(forecast, recentSensorHistory, recentJournalEntries, plants)
        val response = api.sendMessage(
            apiKey = apiKey,
            request = ClaudeRequest(
                system = SCHEDULE_SYSTEM_PROMPT,
                messages = listOf(ClaudeMessage(role = "user", content = prompt))
            )
        )
        val text = response.content.firstOrNull { it.type == "text" }?.text
            ?: error("No text in Claude response")

        val jsonText = runCatching {
            val start = text.indexOf('[')
            val end = text.lastIndexOf(']')
            if (start != -1 && end != -1 && end > start) text.substring(start, end + 1) else text
        }.getOrDefault(text)

        val raw = scheduleJson.decodeFromString<List<RawScheduleTask>>(jsonText)
        val zone = ZoneId.systemDefault()
        raw.mapIndexedNotNull { idx, r ->
            runCatching {
                val date = LocalDate.parse(r.date)
                val time = runCatching { LocalTime.parse(r.time) }.getOrDefault(LocalTime.of(8, 0))
                val epochMs = LocalDateTime.of(date, time).atZone(zone).toInstant().toEpochMilli()
                MaintenanceTask(
                    id = idx.toLong(),
                    description = r.task,
                    scheduledFor = epochMs,
                    plantName = r.plant?.takeIf { it.isNotBlank() && it != "null" },
                    reason = r.reason,
                )
            }.getOrNull()
        }
    }

    private fun buildSchedulePrompt(
        forecast: List<ForecastDay>,
        sensorHistory: List<SensorReading>,
        journalEntries: List<JournalEntry>,
        plants: List<Plant>,
    ): String = buildString {
        val now = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ENGLISH).format(Date())
        appendLine("Today: $now")
        appendLine()

        if (plants.isNotEmpty()) {
            appendLine("PLANTS (${plants.size}):")
            plants.take(15).forEach { p ->
                append("• ${p.commonName}")
                p.scientificName?.let { append(" ($it)") }
                p.locationInGarden?.let { append(" — $it") }
                appendLine()
            }
            appendLine()
        }

        if (sensorHistory.isNotEmpty()) {
            appendLine("SENSOR HISTORY (past 4 weeks, latest per device):")
            sensorHistory.groupBy { it.deviceAddress }.forEach { (_, readings) ->
                val latest = readings.maxByOrNull { it.recordedAt } ?: return@forEach
                appendLine("• ${latest.deviceName ?: "Sensor"}: moisture ${latest.moisturePercent}%, fertility ${latest.fertilityMicroSiemens} µS/cm, temp ${latest.temperatureCelsius}°C")
            }
            appendLine()
        }

        if (journalEntries.isNotEmpty()) {
            appendLine("RECENT JOURNAL ENTRIES (past 4 weeks):")
            journalEntries.take(10).forEach { e ->
                val date = SimpleDateFormat("d MMM", Locale.ENGLISH).format(Date(e.capturedAt))
                val note = e.notes?.take(100) ?: ""
                appendLine("• $date: $note")
            }
            appendLine()
        }

        if (forecast.isNotEmpty()) {
            appendLine("7-DAY FORECAST:")
            appendLine("Date       | Max°C | Min°C | Rain mm | UV  | Evapotransp mm")
            forecast.forEach { d ->
                appendLine("${d.date} | ${d.tempMaxC.toInt()}     | ${d.tempMinC.toInt()}     | ${"%.1f".format(d.precipMm)}     | ${d.uvIndexMax.toInt()}   | ${"%.1f".format(d.evapotranspirationMm)}")
            }
            appendLine()
        }

        appendLine("Generate a maintenance schedule for the next 7 days based on the above data.")
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
