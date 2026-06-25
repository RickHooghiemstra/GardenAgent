package com.gardenagent.data.remote.eufy.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EufyDeviceResponse(
    val code: Int,
    val data: EufyDeviceData? = null,
)

@Serializable
data class EufyDeviceData(
    @SerialName("device_list") val deviceList: List<EufyDeviceDto> = emptyList(),
)

@Serializable
data class EufyDeviceDto(
    @SerialName("device_sn") val deviceSn: String,
    @SerialName("device_name") val deviceName: String,
    @SerialName("device_model") val deviceModel: String,
    @SerialName("device_type") val deviceType: Int,
    @SerialName("local_ip") val localIp: String? = null,
    val status: Int = 0,
)
