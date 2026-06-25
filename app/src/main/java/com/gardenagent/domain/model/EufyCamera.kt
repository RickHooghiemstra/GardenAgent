package com.gardenagent.domain.model

data class EufyCamera(
    val deviceSn: String,
    val deviceName: String,
    val deviceModel: String,
    val deviceType: Int,
    val localIp: String?,
    val isOnline: Boolean,
) {
    val rtspUrl: String? get() = localIp?.let { "rtsp://$it/live0" }
    val isDoorbell: Boolean get() = deviceType == 7
}
