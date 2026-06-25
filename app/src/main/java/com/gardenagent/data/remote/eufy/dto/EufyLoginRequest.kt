package com.gardenagent.data.remote.eufy.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EufyLoginRequest(
    val email: String,
    val password: String,
    val country: String = "NL",
    val timezone: String = "Europe/Amsterdam",
)
