package com.gardenagent.domain.model

data class Plant(
    val id: Long = 0,
    val commonName: String,
    val scientificName: String? = null,
    val family: String? = null,
    val photoPath: String? = null,
    val plantedDate: Long? = null,
    val locationInGarden: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
