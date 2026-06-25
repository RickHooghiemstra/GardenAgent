package com.gardenagent.data.repository

import com.gardenagent.data.remote.plantnet.PlantNetApiService
import com.gardenagent.domain.model.PlantIdentification
import com.gardenagent.domain.repository.PlantIdentificationRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class PlantIdentificationRepositoryImpl @Inject constructor(
    private val api: PlantNetApiService,
    @Named("plantnet_api_key") private val apiKey: String,
) : PlantIdentificationRepository {

    override suspend fun identify(imagePaths: List<String>): Result<List<PlantIdentification>> = runCatching {
        val imageParts = imagePaths.take(5).map { path ->
            val file = File(path)
            MultipartBody.Part.createFormData(
                "images",
                file.name,
                file.asRequestBody("image/jpeg".toMediaType()),
            )
        }
        val organs = "flower,leaf,fruit,bark".toRequestBody("text/plain".toMediaType())

        val response = api.identify(apiKey, images = imageParts, organs = organs)

        response.results
            .filter { it.score > 0.01 }
            .take(5)
            .map { result ->
                PlantIdentification(
                    commonName = result.species.commonNames.firstOrNull() ?: result.species.scientificName,
                    scientificName = result.species.scientificName,
                    family = result.species.family?.scientificName ?: "",
                    confidence = result.score.toFloat(),
                )
            }
    }
}
