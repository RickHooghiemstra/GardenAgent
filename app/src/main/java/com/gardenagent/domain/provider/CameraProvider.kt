package com.gardenagent.domain.provider

import com.gardenagent.domain.model.EufyCamera
import kotlinx.coroutines.flow.Flow

interface CameraProvider {
    fun getCameras(): Flow<List<EufyCamera>>
    suspend fun refreshCameras(): Result<Unit>
    fun getStreamUrl(deviceSn: String): String?
}
