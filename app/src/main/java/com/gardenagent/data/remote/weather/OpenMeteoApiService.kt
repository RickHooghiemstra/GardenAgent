package com.gardenagent.data.remote.weather

import com.gardenagent.data.remote.weather.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApiService {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String,
        @Query("hourly") hourly: String,
        @Query("daily") daily: String? = null,
        @Query("timezone") timezone: String,
    ): WeatherResponse
}
