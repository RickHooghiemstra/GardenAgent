package com.gardenagent.data.remote.eufy.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EufyLoginResponse(
    val code: Int,
    val msg: String? = null,
    val data: EufyLoginData? = null,
)

@Serializable
data class EufyLoginData(
    @SerialName("access_token") val accessToken: String,
    @SerialName("user_id") val userId: String,
)
