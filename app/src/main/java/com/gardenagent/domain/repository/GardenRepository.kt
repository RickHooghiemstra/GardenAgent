package com.gardenagent.domain.repository

import com.gardenagent.domain.model.Garden
import kotlinx.coroutines.flow.Flow

interface GardenRepository {
    fun getGardens(): Flow<List<Garden>>
    suspend fun createGarden(garden: Garden): Long
    suspend fun deleteGarden(garden: Garden)
    suspend fun getGardenById(id: Long): Garden?
}
