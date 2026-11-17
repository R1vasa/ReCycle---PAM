package com.example.recycle.feature.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recycle.core.data.model.Profile
import com.example.recycle.core.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Pemilik: Bersama
// State sesi + role pengguna (FR-AUTH-04).

sealed interface SessionState {
    data object Checking : SessionState
    data object LoggedOut : SessionState
    data class LoggedIn(val email: String?, val role: String) : SessionState {
        val isPengelola: Boolean get() = role == Profile.ROLE_PENGELOLA
    }
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Checking)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    /**
     * State aksi login/register.
     * Success(null) = berhasil dan sudah login; Success(pesan) = berhasil tapi perlu konfirmasi email.
     */
    private val _authState = MutableStateFlow<UiState<String?>>(UiState.Idle)
    val authState: StateFlow<UiState<String?>> = _authState.asStateFlow()

    init {
        checkSession()
    }

    fun checkSession() {
        viewModelScope.launch {
            _sessionState.value = SessionState.Checking
            _sessionState.value = if (repository.restoreSession()) loggedIn() else SessionState.LoggedOut
        }
    }

    fun login(email: String, password: String) {
        val cleanEmail = email.trim()
        validate(email = cleanEmail, password = password)?.let {
            _authState.value = UiState.Error(it)
            return
        }
        viewModelScope.launch {
            _authState.value = UiState.Loading
            repository.login(cleanEmail, password)
                .onSuccess {
                    _authState.value = UiState.Success(null)
                    _sessionState.value = loggedIn()
                }
                .onFailure { _authState.value = UiState.Error(it.message ?: "Login gagal.") }
        }
    }

    fun register(fullName: String, email: String, password: String) {
        val cleanName = fullName.trim()
        val cleanEmail = email.trim()
        val error = when {
            cleanName.isBlank() -> "Nama lengkap wajib diisi."
            else -> validate(email = cleanEmail, password = password)
        }
        if (error != null) {
            _authState.value = UiState.Error(error)
            return
        }
        viewModelScope.launch {
            _authState.value = UiState.Loading
            repository.register(cleanName, cleanEmail, password)
                .onSuccess { loggedInNow ->
                    if (loggedInNow) {
                        _authState.value = UiState.Success(null)
                        _sessionState.value = loggedIn()
                    } else {
                        _authState.value = UiState.Success(
                            "Pendaftaran berhasil. Cek email kamu untuk konfirmasi, lalu masuk."
                        )
                    }
                }
                .onFailure { _authState.value = UiState.Error(it.message ?: "Pendaftaran gagal.") }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _authState.value = UiState.Idle
            _sessionState.value = SessionState.LoggedOut
        }
    }

    /** Panggil saat berpindah antara layar Login dan Register agar pesan lama hilang. */
    fun resetAuthState() {
        _authState.value = UiState.Idle
    }

    private fun loggedIn() = SessionState.LoggedIn(repository.currentEmail(), repository.currentRole())

    private fun validate(email: String, password: String): String? = when {
        email.isBlank() -> "Email wajib diisi."
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Format email tidak valid."
        password.length < 6 -> "Password minimal 6 karakter."
        else -> null
    }
}
