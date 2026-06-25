package com.gardenagent.domain.model

data class PlantIdentification(
    val commonName: String,
    val scientificName: String,
    val family: String,
    val confidence: Float,
)
