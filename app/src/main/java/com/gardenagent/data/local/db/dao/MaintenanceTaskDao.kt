package com.gardenagent.data.local.db.dao

import androidx.room.*
import com.gardenagent.data.local.db.entity.MaintenanceTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceTaskDao {
    @Query("SELECT * FROM maintenance_tasks WHERE scheduledFor >= :nowMs ORDER BY scheduledFor ASC")
    fun getUpcomingTasks(nowMs: Long): Flow<List<MaintenanceTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<MaintenanceTaskEntity>)

    @Query("DELETE FROM maintenance_tasks WHERE scheduledFor < :cutoffMs")
    suspend fun deleteOlderThan(cutoffMs: Long)

    @Query("DELETE FROM maintenance_tasks")
    suspend fun deleteAll()
}
