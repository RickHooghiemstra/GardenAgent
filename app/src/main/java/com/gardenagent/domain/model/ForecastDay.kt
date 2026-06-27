package com.gardenagent.domain.model

data class ForecastDay(
    val date: String,
    val tempMaxC: Float,
    val tempMinC: Float,
    val precipMm: Float,
    val uvIndexMax: Float,
    val evapotranspirationMm: Float,
)
