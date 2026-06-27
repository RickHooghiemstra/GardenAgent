package com.gardenagent.data.repository

import com.gardenagent.data.local.db.dao.MaintenanceTaskDao
import com.gardenagent.data.local.db.entity.toDomain
import com.gardenagent.data.local.db.entity.toEntity
import com.gardenagent.domain.model.MaintenanceTask
import com.gardenagent.domain.repository.MaintenanceTaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MaintenanceTaskRepositoryImpl @Inject constructor(
    private val dao: MaintenanceTaskDao,
) : MaintenanceTaskRepository {

    override fun getUpcomingTasks(): Flow<List<MaintenanceTask>> =
        dao.getUpcomingTasks(System.currentTimeMillis()).map { it.map { e -> e.toDomain() } }

    override suspend fun replaceTasks(tasks: List<MaintenanceTask>) {
        dao.deleteAll()
        dao.insertAll(tasks.map { it.toEntity() })
    }
}
