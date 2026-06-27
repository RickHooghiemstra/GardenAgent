package com.gardenagent.data.repository

import com.gardenagent.data.local.db.dao.ManualCameraDao
import com.gardenagent.data.local.db.entity.toDomain
import com.gardenagent.data.local.db.entity.toEntity
import com.gardenagent.domain.model.ManualCamera
import com.gardenagent.domain.repository.ManualCameraRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ManualCameraRepositoryImpl @Inject constructor(private val dao: ManualCameraDao) : ManualCameraRepository {
    override fun getCameras(): Flow<List<ManualCamera>> = dao.getAll().map { it.map { e -> e.toDomain() } }
    override suspend fun addCamera(camera: ManualCamera): Long = dao.insert(camera.toEntity())
    override suspend fun deleteCamera(camera: ManualCamera) = dao.delete(camera.toEntity())
}
