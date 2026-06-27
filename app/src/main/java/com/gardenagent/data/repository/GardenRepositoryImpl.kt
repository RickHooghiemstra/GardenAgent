package com.gardenagent.data.repository

import com.gardenagent.data.local.db.dao.GardenDao
import com.gardenagent.data.local.db.entity.toDomain
import com.gardenagent.data.local.db.entity.toEntity
import com.gardenagent.domain.model.Garden
import com.gardenagent.domain.repository.GardenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GardenRepositoryImpl @Inject constructor(private val dao: GardenDao) : GardenRepository {
    override fun getGardens(): Flow<List<Garden>> = dao.getAll().map { it.map { e -> e.toDomain() } }
    override suspend fun createGarden(garden: Garden): Long = dao.insert(garden.toEntity())
    override suspend fun deleteGarden(garden: Garden) = dao.delete(garden.toEntity())
    override suspend fun getGardenById(id: Long): Garden? = dao.getById(id)?.toDomain()
}
