package com.gardenagent.data.repository

import com.gardenagent.data.local.preferences.UserPreferencesDataStore
import com.gardenagent.data.remote.eufy.EufyApiService
import com.gardenagent.data.remote.eufy.dto.EufyLoginRequest
import com.gardenagent.domain.model.EufyCamera
import com.gardenagent.domain.repository.EufyRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class EufyRepositoryImpl @Inject constructor(
    private val api: EufyApiService,
    private val dataStore: UserPreferencesDataStore,
) : EufyRepository {

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val response = api.login(EufyLoginRequest(email = email, password = password))
        val data = response.data ?: error("Login failed: ${response.msg}")
        dataStore.saveEufyCredentials(data.accessToken, data.userId)
    }

    override suspend fun getDevices(): Result<List<EufyCamera>> = runCatching {
        val response = api.getDevices()
        response.data?.deviceList?.map {
            EufyCamera(
                deviceSn = it.deviceSn,
                deviceName = it.deviceName,
                deviceModel = it.deviceModel,
                deviceType = it.deviceType,
                localIp = it.localIp,
                isOnline = it.status == 1,
            )
        } ?: emptyList()
    }

    override suspend fun logout() { dataStore.clearEufyCredentials() }

    override fun isLoggedIn(): Boolean = runCatching {
        kotlinx.coroutines.runBlocking { dataStore.eufyToken.first() != null }
    }.getOrDefault(false)
}
