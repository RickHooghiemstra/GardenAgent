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
        val openudid = dataStore.getOrCreateOpenUdid()
        val response = api.login(EufyLoginRequest(email = email, password = password, openudid = openudid))
        if (response.code != 0) {
            val hint = when (response.code) {
                26050 -> "Wrong email or password."
                26003 -> "Eufy sent a verification code to your email. Complete verification in the Eufy app first, then retry here."
                10001 -> "Please verify your email address in the Eufy app before logging in."
                10002 -> "Eufy requires a captcha. Open the Eufy app once to clear it, then retry."
                else -> response.msg ?: "Unknown error (code ${response.code})"
            }
            error(hint)
        }
        val data = response.data ?: error("Login failed: Eufy returned no user data")
        dataStore.saveEufyCredentials(data.accessToken, data.userId, email)
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

    override suspend fun isLoggedIn(): Boolean = dataStore.eufyToken.first() != null

    override suspend fun getLoggedInEmail(): String? = dataStore.eufyEmail.first()
}
