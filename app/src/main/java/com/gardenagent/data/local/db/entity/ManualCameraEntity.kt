package com.gardenagent.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gardenagent.domain.model.ManualCamera

@Entity(tableName = "manual_cameras")
data class ManualCameraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val rtspUrl: String,
    val locationDescription: String?,
    val gardenId: Long?,
)

fun ManualCameraEntity.toDomain() = ManualCamera(id = id, name = name, rtspUrl = rtspUrl, locationDescription = locationDescription, gardenId = gardenId)
fun ManualCamera.toEntity() = ManualCameraEntity(id = id, name = name, rtspUrl = rtspUrl, locationDescription = locationDescription, gardenId = gardenId)
