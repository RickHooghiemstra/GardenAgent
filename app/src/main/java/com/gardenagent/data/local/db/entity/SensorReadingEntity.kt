package com.gardenagent.data.local.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gardenagent.domain.model.SensorReading

@Entity(
    tableName = "sensor_readings",
    indices = [Index("deviceAddress"), Index("recordedAt")],
)
data class SensorReadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceAddress: String,
    val deviceName: String?,
    val plantId: Long?,
    val temperatureCelsius: Float,
    val moisturePercent: Int,
    val lightLux: Int,
    val fertilityMicroSiemens: Int,
    val batteryPercent: Int,
    val recordedAt: Long = System.currentTimeMillis(),
)

fun SensorReadingEntity.toDomain() = SensorReading(
    id = id, deviceAddress = deviceAddress, deviceName = deviceName,
    plantId = plantId, temperatureCelsius = temperatureCelsius,
    moisturePercent = moisturePercent, lightLux = lightLux,
    fertilityMicroSiemens = fertilityMicroSiemens, batteryPercent = batteryPercent,
    recordedAt = recordedAt,
)

fun SensorReading.toEntity() = SensorReadingEntity(
    id = id, deviceAddress = deviceAddress, deviceName = deviceName,
    plantId = plantId, temperatureCelsius = temperatureCelsius,
    moisturePercent = moisturePercent, lightLux = lightLux,
    fertilityMicroSiemens = fertilityMicroSiemens, batteryPercent = batteryPercent,
    recordedAt = recordedAt,
)
