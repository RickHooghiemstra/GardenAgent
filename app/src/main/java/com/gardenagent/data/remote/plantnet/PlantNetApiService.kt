package com.gardenagent.data.remote.plantnet

import com.gardenagent.data.remote.plantnet.dto.PlantNetResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface PlantNetApiService {
    @Multipart
    @POST("v2/identify/all")
    suspend fun identify(
        @Query("api-key") apiKey: String,
        @Query("lang") lang: String = "en",
        @Part images: List<MultipartBody.Part>,
        @Part("organs") organs: okhttp3.RequestBody,
    ): PlantNetResponse
}
