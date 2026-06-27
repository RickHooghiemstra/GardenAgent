package com.gardenagent.domain.usecase.garden

import com.gardenagent.domain.model.Garden
import com.gardenagent.domain.repository.GardenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGardensUseCase @Inject constructor(private val repository: GardenRepository) {
    operator fun invoke(): Flow<List<Garden>> = repository.getGardens()
}
