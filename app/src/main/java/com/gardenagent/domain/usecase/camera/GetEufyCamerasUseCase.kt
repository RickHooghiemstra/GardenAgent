package com.gardenagent.domain.usecase.camera

import com.gardenagent.domain.model.EufyCamera
import com.gardenagent.domain.provider.CameraProvider
import javax.inject.Inject

class GetEufyCamerasUseCase @Inject constructor(
    private val providers: Set<@JvmSuppressWildcards CameraProvider>
) {
    suspend operator fun invoke(): Result<List<EufyCamera>> = runCatching {
        providers.flatMap { provider ->
            provider.refreshCameras().getOrThrow()
            emptyList<EufyCamera>()
        }
        providers.firstOrNull()?.refreshCameras()?.getOrThrow()
        val results = mutableListOf<EufyCamera>()
        providers.forEach { provider ->
            provider.getCameras().collect { cameras -> results.addAll(cameras) }
        }
        results
    }
}
