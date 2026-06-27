package com.gardenagent.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gardenagent.domain.model.Garden
import com.gardenagent.domain.model.GardenType

@Entity(tableName = "gardens")
data class GardenEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val description: String?,
    val createdAt: Long = System.currentTimeMillis(),
)

fun GardenEntity.toDomain() = Garden(id = id, name = name, type = GardenType.valueOf(type), description = description, createdAt = createdAt)
fun Garden.toEntity() = GardenEntity(id = id, name = name, type = type.name, description = description, createdAt = createdAt)
