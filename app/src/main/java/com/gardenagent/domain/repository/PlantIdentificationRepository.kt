package com.gardenagent.domain.repository

import com.gardenagent.domain.model.PlantIdentification

interface PlantIdentificationRepository {
    suspend fun identify(imagePaths: List<String>): Result<List<PlantIdentification>>
}
