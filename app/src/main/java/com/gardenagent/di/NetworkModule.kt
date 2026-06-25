package com.gardenagent.di

import com.gardenagent.BuildConfig
import com.gardenagent.data.local.preferences.UserPreferencesDataStore
import com.gardenagent.data.remote.claude.ClaudeApiService
import com.gardenagent.data.remote.eufy.EufyApiService
import com.gardenagent.data.remote.eufy.EufyAuthInterceptor
import com.gardenagent.data.remote.plantnet.PlantNetApiService
import com.gardenagent.data.remote.weather.OpenMeteoApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier @Retention(AnnotationRetention.BINARY) annotation class EufyOkHttp
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class ClaudeOkHttp

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    private val contentType = "application/json".toMediaType()

    private fun baseOkHttp(): OkHttpClient.Builder = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)

    @Provides @Singleton @Named("claude_api_key")
    fun provideClaudeApiKey(): String = BuildConfig.CLAUDE_API_KEY

    @Provides @Singleton @Named("plantnet_api_key")
    fun providePlantNetApiKey(): String = BuildConfig.PLANTNET_API_KEY

    @Provides @Singleton @EufyOkHttp
    fun provideEufyOkHttp(dataStore: UserPreferencesDataStore): OkHttpClient =
        baseOkHttp()
            .addInterceptor(EufyAuthInterceptor(dataStore))
            .build()

    @Provides @Singleton @ClaudeOkHttp
    fun provideClaudeOkHttp(): OkHttpClient =
        baseOkHttp()
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

    @Provides @Singleton
    fun provideEufyApiService(@EufyOkHttp client: OkHttpClient): EufyApiService =
        Retrofit.Builder()
            .baseUrl("https://home-api.eufylife.com/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(EufyApiService::class.java)

    @Provides @Singleton
    fun provideOpenMeteoApiService(): OpenMeteoApiService =
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(baseOkHttp().build())
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(OpenMeteoApiService::class.java)

    @Provides @Singleton
    fun providePlantNetApiService(): PlantNetApiService =
        Retrofit.Builder()
            .baseUrl("https://my-api.plantnet.org/")
            .client(baseOkHttp().build())
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(PlantNetApiService::class.java)

    @Provides @Singleton
    fun provideClaudeApiService(@ClaudeOkHttp client: OkHttpClient): ClaudeApiService =
        Retrofit.Builder()
            .baseUrl("https://api.anthropic.com/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(ClaudeApiService::class.java)
}
