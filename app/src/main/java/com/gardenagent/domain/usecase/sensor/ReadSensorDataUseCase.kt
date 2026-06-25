package com.gardenagent.domain.usecase.sensor

import com.gardenagent.domain.model.SensorReading
import com.gardenagent.domain.provider.SoilSensorProvider
import com.gardenagent.domain.repository.SensorRepository
import javax.inject.Inject

class ReadSensorDataUseCase @Inject constructor(
    private val providers: Set<@JvmSuppressWildcards SoilSensorProvider>,
    private val repository: SensorRepository,
) {
    suspend operator fun invoke(address: String, name: String?): Result<SensorReading> {
        val provider = providers.firstOrNull() ?: return Result.failure(IllegalStateException("No sensor provider available"))
        return provider.readSensor(address, name).onSuccess { repository.saveReading(it) }
    }
}
