package com.gardenagent.data.remote.eufy

import com.gardenagent.data.local.preferences.UserPreferencesDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.util.TimeZone
import javax.inject.Inject

class EufyAuthInterceptor @Inject constructor(
    private val dataStore: UserPreferencesDataStore,
) : Interceptor {

    // UDID is stable once generated; cache it to avoid repeated DataStore reads on the OkHttp thread.
    @Volatile private var cachedUdid: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { dataStore.getEufyToken() }
        val udid = cachedUdid ?: runBlocking { dataStore.getOrCreateOpenUdid() }.also { cachedUdid = it }
        val request = chain.request().newBuilder()
            .apply {
                if (token != null) addHeader("Authorization", "Bearer $token")
            }
            .addHeader("openudid", udid)
            .addHeader("timezone", TimeZone.getDefault().id)
            .addHeader("language", java.util.Locale.getDefault().language)
            .addHeader("content-type", "application/json")
            .build()
        return chain.proceed(request)
    }
}
