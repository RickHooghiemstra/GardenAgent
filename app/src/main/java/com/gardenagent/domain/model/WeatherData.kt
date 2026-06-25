package com.gardenagent.domain.model

data class WeatherData(
    val id: Long = 0,
    val temperatureCelsius: Float,
    val humidity: Int,
    val precipitationMm: Float,
    val uvIndex: Float,
    val soilTemperatureCelsius: Float? = null,
    val latitude: Double,
    val longitude: Double,
    val recordedAt: Long = System.currentTimeMillis(),
)
