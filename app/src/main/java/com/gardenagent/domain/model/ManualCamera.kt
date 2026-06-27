package com.gardenagent.domain.model

data class ManualCamera(
    val id: Long = 0,
    val name: String,
    val rtspUrl: String,
    val locationDescription: String? = null,
    val gardenId: Long? = null,
)
