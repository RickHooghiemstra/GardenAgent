package com.gardenagent.domain.usecase.garden

import com.gardenagent.domain.model.Garden
import com.gardenagent.domain.repository.GardenRepository
import javax.inject.Inject

class DeleteGardenUseCase @Inject constructor(private val repository: GardenRepository) {
    suspend operator fun invoke(garden: Garden) = repository.deleteGarden(garden)
}
