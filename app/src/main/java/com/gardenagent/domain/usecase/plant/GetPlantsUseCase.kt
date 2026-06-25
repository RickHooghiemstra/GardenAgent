package com.gardenagent.domain.usecase.plant

import com.gardenagent.domain.model.Plant
import com.gardenagent.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlantsUseCase @Inject constructor(private val repository: PlantRepository) {
    operator fun invoke(): Flow<List<Plant>> = repository.getAllPlants()
}
