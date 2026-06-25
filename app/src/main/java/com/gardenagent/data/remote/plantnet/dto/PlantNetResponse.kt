package com.gardenagent.data.remote.plantnet.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlantNetResponse(
    val results: List<PlantNetResult> = emptyList(),
    @SerialName("remainingIdentificationRequests") val remainingRequests: Int? = null,
)

@Serializable
data class PlantNetResult(
    val score: Double,
    val species: PlantNetSpecies,
)

@Serializable
data class PlantNetSpecies(
    @SerialName("scientificNameWithoutAuthor") val scientificName: String,
    @SerialName("commonNames") val commonNames: List<String> = emptyList(),
    val family: PlantNetTaxon? = null,
)

@Serializable
data class PlantNetTaxon(
    @SerialName("scientificNameWithoutAuthor") val scientificName: String,
)
