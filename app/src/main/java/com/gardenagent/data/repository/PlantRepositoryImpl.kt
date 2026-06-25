package com.gardenagent.data.repository

import com.gardenagent.data.local.db.dao.PlantDao
import com.gardenagent.data.local.db.entity.toDomain
import com.gardenagent.data.local.db.entity.toEntity
import com.gardenagent.domain.model.Plant
import com.gardenagent.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlantRepositoryImpl @Inject constructor(private val dao: PlantDao) : PlantRepository {
    override fun getAllPlants(): Flow<List<Plant>> = dao.getAllPlants().map { list -> list.map { it.toDomain() } }
    override suspend fun getPlantById(id: Long): Plant? = dao.getPlantById(id)?.toDomain()
    override suspend fun insertPlant(plant: Plant): Long = dao.insertPlant(plant.toEntity())
    override suspend fun updatePlant(plant: Plant) = dao.updatePlant(plant.toEntity())
    override suspend fun deletePlant(plant: Plant) = dao.deletePlant(plant.toEntity())
}
