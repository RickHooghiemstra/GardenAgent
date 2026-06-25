package com.gardenagent

import com.gardenagent.data.remote.claude.ClaudeApiService
import com.gardenagent.data.repository.GardenAdviceRepositoryImpl
import com.gardenagent.domain.model.Plant
import com.gardenagent.domain.model.SensorReading
import com.gardenagent.domain.model.WeatherData
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test

class GardenAdvicePromptTest {

    private val repo = GardenAdviceRepositoryImpl(mockk<ClaudeApiService>(), "test-key")

    @Test
    fun `buildPrompt includes weather data`() {
        val weather = WeatherData(
            temperatureCelsius = 22f,
            humidity = 65,
            precipitationMm = 0f,
            uvIndex = 5.5f,
            latitude = 52.0,
            longitude = 4.9,
        )
        val prompt = repo.buildPrompt(weather, emptyList(), emptyList())
        assertTrue(prompt.contains("22°C"))
        assertTrue(prompt.contains("65%"))
        assertTrue(prompt.contains("5.5"))
    }

    @Test
    fun `buildPrompt includes sensor readings`() {
        val reading = SensorReading(
            deviceAddress = "AA:BB:CC:DD:EE:FF",
            deviceName = "Garden Sensor",
            temperatureCelsius = 18f,
            moisturePercent = 42,
            lightLux = 800,
            fertilityMicroSiemens = 150,
            batteryPercent = 90,
        )
        val prompt = repo.buildPrompt(null, listOf(reading), emptyList())
        assertTrue(prompt.contains("moisture 42%"))
        assertTrue(prompt.contains("Garden Sensor"))
    }

    @Test
    fun `buildPrompt includes plant names`() {
        val plants = listOf(
            Plant(commonName = "Tomato", scientificName = "Solanum lycopersicum"),
            Plant(commonName = "Basil"),
        )
        val prompt = repo.buildPrompt(null, emptyList(), plants)
        assertTrue(prompt.contains("Tomato"))
        assertTrue(prompt.contains("Basil"))
    }

    @Test
    fun `buildPrompt handles empty data gracefully`() {
        val prompt = repo.buildPrompt(null, emptyList(), emptyList())
        assertTrue(prompt.isNotBlank())
        assertTrue(prompt.contains("advice"))
    }
}
