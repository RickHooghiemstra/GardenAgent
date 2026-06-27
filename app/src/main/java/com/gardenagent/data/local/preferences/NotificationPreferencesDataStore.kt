package com.gardenagent.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.notifDataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_prefs")

data class NotificationPreferences(
    val wateringEnabled: Boolean = true,
    val wateringDaysInterval: Int = 3,
    val fertilizingEnabled: Boolean = true,
    val fertilizingDaysInterval: Int = 14,
    val mowingEnabled: Boolean = false,
    val mowingDaysInterval: Int = 7,
    val compostEnabled: Boolean = false,
    val compostDaysInterval: Int = 30,
    val pesticidesEnabled: Boolean = false,
    val pesticidesDaysInterval: Int = 14,
    val reminderHour: Int = 8,
    val reminderMinute: Int = 0,
)

@Singleton
class NotificationPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val wateringEnabledKey = booleanPreferencesKey("watering_enabled")
    private val wateringDaysKey = intPreferencesKey("watering_days")
    private val fertilizingEnabledKey = booleanPreferencesKey("fertilizing_enabled")
    private val fertilizingDaysKey = intPreferencesKey("fertilizing_days")
    private val mowingEnabledKey = booleanPreferencesKey("mowing_enabled")
    private val mowingDaysKey = intPreferencesKey("mowing_days")
    private val compostEnabledKey = booleanPreferencesKey("compost_enabled")
    private val compostDaysKey = intPreferencesKey("compost_days")
    private val pesticidesEnabledKey = booleanPreferencesKey("pesticides_enabled")
    private val pesticidesDaysKey = intPreferencesKey("pesticides_days")
    private val reminderHourKey = intPreferencesKey("reminder_hour")
    private val reminderMinuteKey = intPreferencesKey("reminder_minute")

    val preferences: Flow<NotificationPreferences> = context.notifDataStore.data.map { prefs ->
        NotificationPreferences(
            wateringEnabled = prefs[wateringEnabledKey] ?: true,
            wateringDaysInterval = prefs[wateringDaysKey] ?: 3,
            fertilizingEnabled = prefs[fertilizingEnabledKey] ?: true,
            fertilizingDaysInterval = prefs[fertilizingDaysKey] ?: 14,
            mowingEnabled = prefs[mowingEnabledKey] ?: false,
            mowingDaysInterval = prefs[mowingDaysKey] ?: 7,
            compostEnabled = prefs[compostEnabledKey] ?: false,
            compostDaysInterval = prefs[compostDaysKey] ?: 30,
            pesticidesEnabled = prefs[pesticidesEnabledKey] ?: false,
            pesticidesDaysInterval = prefs[pesticidesDaysKey] ?: 14,
            reminderHour = prefs[reminderHourKey] ?: 8,
            reminderMinute = prefs[reminderMinuteKey] ?: 0,
        )
    }

    suspend fun update(transform: (NotificationPreferences) -> NotificationPreferences) {
        context.notifDataStore.edit { prefs ->
            val before = NotificationPreferences(
                wateringEnabled = prefs[wateringEnabledKey] ?: true,
                wateringDaysInterval = prefs[wateringDaysKey] ?: 3,
                fertilizingEnabled = prefs[fertilizingEnabledKey] ?: true,
                fertilizingDaysInterval = prefs[fertilizingDaysKey] ?: 14,
                mowingEnabled = prefs[mowingEnabledKey] ?: false,
                mowingDaysInterval = prefs[mowingDaysKey] ?: 7,
                compostEnabled = prefs[compostEnabledKey] ?: false,
                compostDaysInterval = prefs[compostDaysKey] ?: 30,
                pesticidesEnabled = prefs[pesticidesEnabledKey] ?: false,
                pesticidesDaysInterval = prefs[pesticidesDaysKey] ?: 14,
                reminderHour = prefs[reminderHourKey] ?: 8,
                reminderMinute = prefs[reminderMinuteKey] ?: 0,
            )
            val after = transform(before)
            prefs[wateringEnabledKey] = after.wateringEnabled
            prefs[wateringDaysKey] = after.wateringDaysInterval
            prefs[fertilizingEnabledKey] = after.fertilizingEnabled
            prefs[fertilizingDaysKey] = after.fertilizingDaysInterval
            prefs[mowingEnabledKey] = after.mowingEnabled
            prefs[mowingDaysKey] = after.mowingDaysInterval
            prefs[compostEnabledKey] = after.compostEnabled
            prefs[compostDaysKey] = after.compostDaysInterval
            prefs[pesticidesEnabledKey] = after.pesticidesEnabled
            prefs[pesticidesDaysKey] = after.pesticidesDaysInterval
            prefs[reminderHourKey] = after.reminderHour
            prefs[reminderMinuteKey] = after.reminderMinute
        }
    }
}
