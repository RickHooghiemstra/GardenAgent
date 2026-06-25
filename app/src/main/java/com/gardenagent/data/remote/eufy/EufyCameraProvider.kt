package com.gardenagent.data.remote.eufy

import com.gardenagent.data.local.preferences.UserPreferencesDataStore
import com.gardenagent.data.remote.eufy.dto.EufyDeviceDto
import com.gardenagent.domain.model.EufyCamera
import com.gardenagent.domain.provider.CameraProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EufyCameraProvider @Inject constructor(
    private val api: EufyApiService,
    private val dataStore: UserPreferencesDataStore,
) : CameraProvider {

    private val _cameras = MutableStateFlow<List<EufyCamera>>(emptyList())

    override fun getCameras(): Flow<List<EufyCamera>> = _cameras

    override suspend fun refreshCameras(): Result<Unit> = runCatching {
        val response = api.getDevices()
        _cameras.value = response.data?.deviceList?.map { it.toDomain() } ?: emptyList()
    }

    override fun getStreamUrl(deviceSn: String): String? =
        _cameras.value.find { it.deviceSn == deviceSn }?.rtspUrl

    private fun EufyDeviceDto.toDomain() = EufyCamera(
        deviceSn = deviceSn,
        deviceName = deviceName,
        deviceModel = deviceModel,
        deviceType = deviceType,
        localIp = localIp,
        isOnline = status == 1,
    )
}
