package com.gardenagent.data.repository

import com.gardenagent.data.local.db.dao.SensorDeviceDao
import com.gardenagent.data.local.db.entity.toDomain
import com.gardenagent.data.local.db.entity.toEntity
import com.gardenagent.domain.model.SensorDevice
import com.gardenagent.domain.repository.SensorDeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SensorDeviceRepositoryImpl @Inject constructor(private val dao: SensorDeviceDao) : SensorDeviceRepository {
    override fun getDevices(): Flow<List<SensorDevice>> = dao.getAll().map { it.map { e -> e.toDomain() } }
    override suspend fun getDeviceByAddress(address: String): SensorDevice? = dao.getByAddress(address)?.toDomain()
    override suspend fun registerDevice(device: SensorDevice) = dao.insert(device.toEntity())
    override suspend fun deleteDevice(device: SensorDevice) = dao.delete(device.toEntity())
}
