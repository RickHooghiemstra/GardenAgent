package com.gardenagent.domain.model

enum class GardenType { OUTDOOR, INDOOR, BALCONY, GREENHOUSE }

data class Garden(
    val id: Long = 0,
    val name: String,
    val type: GardenType = GardenType.OUTDOOR,
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)
