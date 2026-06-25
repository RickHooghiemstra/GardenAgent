package com.gardenagent.domain.usecase.sensor

import com.gardenagent.domain.provider.SensorScanResult
import com.gardenagent.domain.provider.SoilSensorProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

class ScanForSensorsUseCase @Inject constructor(
    private val providers: Set<@JvmSuppressWildcards SoilSensorProvider>
) {
    operator fun invoke(): Flow<SensorScanResult> =
        providers.map { it.scan() }.merge()
}
