package com.gardenagent.data.repository

import com.gardenagent.data.local.db.dao.SensorReadingDao
import com.gardenagent.data.local.db.entity.toDomain
import com.gardenagent.data.local.db.entity.toEntity
import com.gardenagent.domain.model.SensorReading
import com.gardenagent.domain.repository.SensorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SensorRepositoryImpl @Inject constructor(private val dao: SensorReadingDao) : SensorRepository {
    override fun getRecentReadings(limit: Int): Flow<List<SensorReading>> = dao.getRecentReadings(limit).map { it.map { r -> r.toDomain() } }
    override fun getLatestReadingForDevice(address: String): Flow<SensorReading?> = dao.getLatestReadingForDevice(address).map { it?.toDomain() }
    override suspend fun saveReading(reading: SensorReading) = dao.insertReading(reading.toEntity())
    override suspend fun deleteOldReadings(cutoffMs: Long) = dao.deleteOldReadings(cutoffMs)
}
