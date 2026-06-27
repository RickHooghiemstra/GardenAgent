package com.gardenagent.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gardenagent.domain.model.JournalEntry

@Entity(
    tableName = "journal_entries",
    foreignKeys = [ForeignKey(
        entity = PlantEntity::class,
        parentColumns = ["id"],
        childColumns = ["plantId"],
        onDelete = ForeignKey.SET_NULL,
    )],
    indices = [Index("plantId"), Index("capturedAt")],
)
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long?,
    val photoPath: String?,
    val notes: String?,
    val capturedAt: Long = System.currentTimeMillis(),
    val weatherCondition: String?,
    val temperatureCelsius: Float?,
    val gardenId: Long? = null,
    val isAiGenerated: Boolean = false,
)

fun JournalEntryEntity.toDomain() = JournalEntry(
    id = id, plantId = plantId, photoPath = photoPath, notes = notes,
    capturedAt = capturedAt, weatherCondition = weatherCondition,
    temperatureCelsius = temperatureCelsius, gardenId = gardenId, isAiGenerated = isAiGenerated,
)

fun JournalEntry.toEntity() = JournalEntryEntity(
    id = id, plantId = plantId, photoPath = photoPath, notes = notes,
    capturedAt = capturedAt, weatherCondition = weatherCondition,
    temperatureCelsius = temperatureCelsius, gardenId = gardenId, isAiGenerated = isAiGenerated,
)
