package com.gardenagent.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val eufyTokenKey = stringPreferencesKey("eufy_token")
    private val eufyUserIdKey = stringPreferencesKey("eufy_user_id")
    private val eufyEmailKey = stringPreferencesKey("eufy_email")
    private val openUdidKey = stringPreferencesKey("open_udid")

    val eufyToken: Flow<String?> = context.dataStore.data.map { it[eufyTokenKey] }
    val eufyUserId: Flow<String?> = context.dataStore.data.map { it[eufyUserIdKey] }
    val eufyEmail: Flow<String?> = context.dataStore.data.map { it[eufyEmailKey] }

    suspend fun saveEufyCredentials(token: String, userId: String, email: String = "") {
        context.dataStore.edit { prefs ->
            prefs[eufyTokenKey] = token
            prefs[eufyUserIdKey] = userId
            prefs[eufyEmailKey] = email
        }
    }

    suspend fun clearEufyCredentials() {
        context.dataStore.edit { prefs ->
            prefs.remove(eufyTokenKey)
            prefs.remove(eufyUserIdKey)
            prefs.remove(eufyEmailKey)
        }
    }

    suspend fun getOrCreateOpenUdid(): String {
        val prefs = context.dataStore.updateData { current ->
            if (current[openUdidKey] != null) current
            else current.toMutablePreferences().apply {
                this[openUdidKey] = UUID.randomUUID().toString().replace("-", "")
            }
        }
        return prefs[openUdidKey]!!
    }

    suspend fun getEufyToken(): String? = context.dataStore.data.first()[eufyTokenKey]
}
