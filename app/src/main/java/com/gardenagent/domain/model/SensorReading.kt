package com.gardenagent.domain.model

data class SensorReading(
    val id: Long = 0,
    val deviceAddress: String,
    val deviceName: String? = null,
    val plantId: Long? = null,
    val temperatureCelsius: Float,
    val moisturePercent: Int,
    val lightLux: Int,
    val fertilityMicroSiemens: Int,
    val batteryPercent: Int,
    val recordedAt: Long = System.currentTimeMillis(),
)
