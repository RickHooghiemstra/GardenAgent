package com.gardenagent.domain.usecase.plant

import com.gardenagent.domain.model.Plant
import com.gardenagent.domain.repository.PlantRepository
import javax.inject.Inject

class AddPlantUseCase @Inject constructor(private val repository: PlantRepository) {
    suspend operator fun invoke(plant: Plant): Long = repository.insertPlant(plant)
}
