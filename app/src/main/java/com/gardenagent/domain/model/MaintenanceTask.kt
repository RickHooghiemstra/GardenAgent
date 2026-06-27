package com.gardenagent.domain.model

data class MaintenanceTask(
    val id: Long = 0,
    val description: String,
    val scheduledFor: Long,
    val plantName: String?,
    val reason: String,
    val workManagerId: String? = null,
)
