package com.gardenagent.data.repository

import android.annotation.SuppressLint
import com.gardenagent.data.local.db.dao.WeatherReadingDao
import com.gardenagent.data.local.db.entity.toDomain
import com.gardenagent.data.local.db.entity.toEntity
import com.gardenagent.data.remote.weather.OpenMeteoApiService
import com.gardenagent.domain.model.WeatherData
import com.gardenagent.domain.repository.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

private const val CACHE_TTL_MS = 30 * 60 * 1000L

class WeatherRepositoryImpl @Inject constructor(
    private val dao: WeatherReadingDao,
    private val api: OpenMeteoApiService,
    private val locationClient: FusedLocationProviderClient,
) : WeatherRepository {

    override fun getLatestWeather(): Flow<WeatherData?> = dao.getLatestReading().map { it?.toDomain() }

    override fun getRecentWeather(limit: Int): Flow<List<WeatherData>> = dao.getRecentReadings(limit).map { it.map { r -> r.toDomain() } }

    @SuppressLint("MissingPermission")
    override suspend fun refreshWeather(): Result<WeatherData> = runCatching {
        val latest = dao.getLatestReading().first()
        if (latest != null && System.currentTimeMillis() - latest.recordedAt < CACHE_TTL_MS) {
            return@runCatching latest.toDomain()
        }

        val location = suspendCancellableCoroutine { cont ->
            locationClient.lastLocation.addOnSuccessListener { loc -> cont.resume(loc) }
                .addOnFailureListener { cont.resume(null) }
        }

        val lat = location?.latitude ?: 52.3676
        val lon = location?.longitude ?: 4.9041

        val response = api.getForecast(
            latitude = lat,
            longitude = lon,
            current = "temperature_2m,relative_humidity_2m,precipitation,uv_index",
            hourly = "soil_temperature_0cm",
            timezone = "auto",
        )

        val current = response.current
        val soilTemp = response.hourly?.soilTemperature0cm?.firstOrNull()

        val data = WeatherData(
            temperatureCelsius = current.temperature2m,
            humidity = current.relativeHumidity2m,
            precipitationMm = current.precipitation,
            uvIndex = current.uvIndex,
            soilTemperatureCelsius = soilTemp,
            latitude = lat,
            longitude = lon,
        )
        dao.insertReading(data.toEntity())
        data
    }
}
