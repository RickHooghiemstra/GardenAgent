package com.gardenagent.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.repository.EufyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val email: String = "",
    val password: String = "",
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val eufyRepository: EufyRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoggedIn = eufyRepository.isLoggedIn())
        }
    }

    fun onEmailChange(email: String) { _uiState.value = _uiState.value.copy(email = email) }
    fun onPasswordChange(pw: String) { _uiState.value = _uiState.value.copy(password = pw) }

    fun login() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "Email and password required")
            return
        }
        _uiState.value = state.copy(isLoading = true, error = null)
        viewModelScope.launch {
            eufyRepository.login(state.email.trim(), state.password)
                .onSuccess { _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true, password = "") }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            eufyRepository.logout()
            _uiState.value = _uiState.value.copy(isLoggedIn = false, email = "", password = "")
        }
    }
}
