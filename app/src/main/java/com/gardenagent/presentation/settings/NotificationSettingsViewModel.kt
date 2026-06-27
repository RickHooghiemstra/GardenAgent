package com.gardenagent.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.data.local.preferences.NotificationPreferences
import com.gardenagent.data.local.preferences.NotificationPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val dataStore: NotificationPreferencesDataStore,
) : ViewModel() {

    val preferences: StateFlow<NotificationPreferences> = dataStore.preferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationPreferences())

    fun update(transform: (NotificationPreferences) -> NotificationPreferences) {
        viewModelScope.launch { dataStore.update(transform) }
    }
}
