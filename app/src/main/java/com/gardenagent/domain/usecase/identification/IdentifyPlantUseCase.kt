package com.gardenagent.domain.usecase.identification

import com.gardenagent.domain.model.PlantIdentification
import com.gardenagent.domain.repository.PlantIdentificationRepository
import javax.inject.Inject

class IdentifyPlantUseCase @Inject constructor(
    private val repository: PlantIdentificationRepository
) {
    suspend operator fun invoke(imagePaths: List<String>): Result<List<PlantIdentification>> =
        repository.identify(imagePaths)
}
