package com.gardenagent.data.local.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gardenagent.domain.model.WeatherData

@Entity(
    tableName = "weather_readings",
    indices = [Index("recordedAt")],
)
data class WeatherReadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val temperatureCelsius: Float,
    val humidity: Int,
    val precipitationMm: Float,
    val uvIndex: Float,
    val soilTemperatureCelsius: Float?,
    val latitude: Double,
    val longitude: Double,
    val recordedAt: Long = System.currentTimeMillis(),
)

fun WeatherReadingEntity.toDomain() = WeatherData(
    id = id, temperatureCelsius = temperatureCelsius, humidity = humidity,
    precipitationMm = precipitationMm, uvIndex = uvIndex,
    soilTemperatureCelsius = soilTemperatureCelsius,
    latitude = latitude, longitude = longitude, recordedAt = recordedAt,
)

fun WeatherData.toEntity() = WeatherReadingEntity(
    id = id, temperatureCelsius = temperatureCelsius, humidity = humidity,
    precipitationMm = precipitationMm, uvIndex = uvIndex,
    soilTemperatureCelsius = soilTemperatureCelsius,
    latitude = latitude, longitude = longitude, recordedAt = recordedAt,
)
