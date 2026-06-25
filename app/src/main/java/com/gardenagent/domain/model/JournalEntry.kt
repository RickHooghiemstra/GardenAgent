package com.gardenagent.domain.model

data class JournalEntry(
    val id: Long = 0,
    val plantId: Long? = null,
    val photoPath: String? = null,
    val notes: String? = null,
    val capturedAt: Long = System.currentTimeMillis(),
    val weatherCondition: String? = null,
    val temperatureCelsius: Float? = null,
)
