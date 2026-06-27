package com.gardenagent.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gardenagent.domain.model.Plant

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val commonName: String,
    val scientificName: String?,
    val family: String?,
    val photoPath: String?,
    val plantedDate: Long?,
    val locationInGarden: String?,
    val notes: String?,
    val gardenId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

fun PlantEntity.toDomain() = Plant(
    id = id, commonName = commonName, scientificName = scientificName,
    family = family, photoPath = photoPath, plantedDate = plantedDate,
    locationInGarden = locationInGarden, notes = notes, gardenId = gardenId,
    createdAt = createdAt, updatedAt = updatedAt,
)

fun Plant.toEntity() = PlantEntity(
    id = id, commonName = commonName, scientificName = scientificName,
    family = family, photoPath = photoPath, plantedDate = plantedDate,
    locationInGarden = locationInGarden, notes = notes, gardenId = gardenId,
    createdAt = createdAt, updatedAt = updatedAt,
)
