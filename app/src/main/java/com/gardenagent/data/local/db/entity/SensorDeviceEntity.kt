package com.gardenagent.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gardenagent.domain.model.SensorDevice

@Entity(tableName = "sensor_devices")
data class SensorDeviceEntity(
    @PrimaryKey val address: String,
    val deviceName: String,
    val locationDescription: String?,
    val gardenId: Long?,
)

fun SensorDeviceEntity.toDomain() = SensorDevice(address = address, deviceName = deviceName, locationDescription = locationDescription, gardenId = gardenId)
fun SensorDevice.toEntity() = SensorDeviceEntity(address = address, deviceName = deviceName, locationDescription = locationDescription, gardenId = gardenId)
