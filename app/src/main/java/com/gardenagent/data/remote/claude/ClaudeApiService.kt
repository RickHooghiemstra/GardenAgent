package com.gardenagent.data.remote.claude

import com.gardenagent.data.remote.claude.dto.ClaudeRequest
import com.gardenagent.data.remote.claude.dto.ClaudeResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ClaudeApiService {
    @Headers("anthropic-version: 2023-06-01")
    @POST("v1/messages")
    suspend fun sendMessage(
        @Header("x-api-key") apiKey: String,
        @Body request: ClaudeRequest,
    ): ClaudeResponse
}
