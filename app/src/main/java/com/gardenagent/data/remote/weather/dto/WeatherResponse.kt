package com.gardenagent.data.remote.weather.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val current: CurrentWeather,
    val hourly: HourlyData? = null,
    val daily: DailyForecast? = null,
)

@Serializable
data class CurrentWeather(
    @SerialName("temperature_2m") val temperature2m: Float,
    @SerialName("relative_humidity_2m") val relativeHumidity2m: Int,
    @SerialName("precipitation") val precipitation: Float,
    @SerialName("uv_index") val uvIndex: Float,
)

@Serializable
data class HourlyData(
    @SerialName("soil_temperature_0cm") val soilTemperature0cm: List<Float>? = null,
)

@Serializable
data class DailyForecast(
    val time: List<String>,
    @SerialName("temperature_2m_max") val tempMax: List<Float>,
    @SerialName("temperature_2m_min") val tempMin: List<Float>,
    @SerialName("precipitation_sum") val precipSum: List<Float>,
    @SerialName("uv_index_max") val uvIndexMax: List<Float>,
    @SerialName("et0_fao_evapotranspiration") val evapotranspiration: List<Float>,
)
