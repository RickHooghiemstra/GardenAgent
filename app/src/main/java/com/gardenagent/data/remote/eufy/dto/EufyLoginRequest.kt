package com.gardenagent.data.remote.eufy.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EufyLoginRequest(
    val email: String,
    val password: String,
    val openudid: String,
    val country: String = java.util.Locale.getDefault().country.takeIf { it.isNotEmpty() } ?: "US",
    val timezone: String = java.util.TimeZone.getDefault().id,
    val lang: String = java.util.Locale.getDefault().language.takeIf { it.isNotEmpty() } ?: "en",
    @SerialName("phone_code") val phoneCode: String = java.util.Locale.getDefault().country.takeIf { it.isNotEmpty() } ?: "1",
    @SerialName("client_id") val clientId: String = "eufyhome-app",
    @SerialName("client_secret") val clientSecret: String = "GQF6xedVqnStFriR",
)
