package com.gardenagent.domain.model

data class GardenAdvice(
    val text: String,
    val generatedAt: Long = System.currentTimeMillis(),
    val basedOnWeather: Boolean = false,
    val basedOnSensors: Boolean = false,
)
