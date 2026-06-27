package com.gardenagent.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gardenagent.domain.model.MaintenanceTask

@Entity(tableName = "maintenance_tasks")
data class MaintenanceTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val scheduledFor: Long,
    val plantName: String?,
    val reason: String,
    val workManagerId: String? = null,
)

fun MaintenanceTaskEntity.toDomain() = MaintenanceTask(
    id = id,
    description = description,
    scheduledFor = scheduledFor,
    plantName = plantName,
    reason = reason,
    workManagerId = workManagerId,
)

fun MaintenanceTask.toEntity() = MaintenanceTaskEntity(
    id = id,
    description = description,
    scheduledFor = scheduledFor,
    plantName = plantName,
    reason = reason,
    workManagerId = workManagerId,
)
