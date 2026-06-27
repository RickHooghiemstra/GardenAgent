package com.gardenagent.domain.repository

import com.gardenagent.domain.model.ManualCamera
import kotlinx.coroutines.flow.Flow

interface ManualCameraRepository {
    fun getCameras(): Flow<List<ManualCamera>>
    suspend fun addCamera(camera: ManualCamera): Long
    suspend fun deleteCamera(camera: ManualCamera)
}
