package com.gardenagent.domain.model

data class SensorDevice(
    val address: String,
    val deviceName: String,
    val locationDescription: String? = null,
    val gardenId: Long? = null,
)
