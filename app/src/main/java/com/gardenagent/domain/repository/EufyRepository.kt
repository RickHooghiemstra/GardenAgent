package com.gardenagent.domain.repository

import com.gardenagent.domain.model.EufyCamera

interface EufyRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun getDevices(): Result<List<EufyCamera>>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}
