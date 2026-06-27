package com.gardenagent.domain.repository

import com.gardenagent.domain.model.MaintenanceTask
import kotlinx.coroutines.flow.Flow

interface MaintenanceTaskRepository {
    fun getUpcomingTasks(): Flow<List<MaintenanceTask>>
    suspend fun replaceTasks(tasks: List<MaintenanceTask>)
}
