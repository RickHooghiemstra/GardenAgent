package com.gardenagent.data.remote.eufy

import com.gardenagent.data.remote.eufy.dto.EufyDeviceResponse
import com.gardenagent.data.remote.eufy.dto.EufyLoginRequest
import com.gardenagent.data.remote.eufy.dto.EufyLoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface EufyApiService {
    @POST("v1/user/v2/email/login")
    suspend fun login(@Body request: EufyLoginRequest): EufyLoginResponse

    @GET("v1/device/v2")
    suspend fun getDevices(): EufyDeviceResponse
}
