package com.example.recycle.feature.auth

import com.example.recycle.core.data.local.SessionManager
import com.example.recycle.core.data.model.AuthResponse
import com.example.recycle.core.data.model.LoginRequest
import com.example.recycle.core.data.model.Profile
import com.example.recycle.core.data.model.RefreshTokenRequest
import com.example.recycle.core.data.model.SignUpMetadata
import com.example.recycle.core.data.model.SignUpRequest
import com.example.recycle.core.data.remote.AuthApiService
import com.example.recycle.core.data.remote.ProfileApiService
import com.example.recycle.core.data.remote.SupabaseProvider
import com.example.recycle.core.data.remote.errorMessage
import com.example.recycle.core.data.remote.toUserMessage
import java.io.IOException

// Pemilik: Bersama
// Register, login, logout, sesi (FR-AUTH-01..03).
class AuthRepository(
    private val authApi: AuthApiService = SupabaseProvider.authService,
    private val profileApi: ProfileApiService = SupabaseProvider.profileService,
    private val session: SessionManager = SessionManager
) {

    /**
     * FR-AUTH-01. Mengembalikan true jika langsung login,
     * false jika Supabase meminta konfirmasi email dulu.
     */
    suspend fun register(fullName: String, email: String, password: String): Result<Boolean> = safeCall {
        val response = authApi.signUp(SignUpRequest(email, password, SignUpMetadata(fullName)))
        if (!response.isSuccessful) error(response.errorMessage())
        val body = response.body() ?: error("Respons kosong dari server.")
        if (body.accessToken == null) {
            false
        } else {
            startSession(body)
            true
        }
    }

    /** FR-AUTH-02. */
    suspend fun login(email: String, password: String): Result<Unit> = safeCall {
        val response = authApi.login(LoginRequest(email, password))
        if (!response.isSuccessful) error(response.errorMessage())
        startSession(response.body() ?: error("Respons kosong dari server."))
    }

    /**
     * FR-AUTH-03. Dipanggil saat Splash: memperbarui token dari refresh_token yang tersimpan.
     * Tanpa internet, sesi lama tetap dipakai agar pengguna tidak terlempar ke Login.
     */
    suspend fun restoreSession(): Boolean {
        val refreshToken = session.refreshToken ?: return false
        return try {
            val response = authApi.refreshToken(RefreshTokenRequest(refreshToken))
            val body = response.body()
            if (response.isSuccessful && body != null) {
                startSession(body)
                true
            } else {
                session.clear()
                false
            }
        } catch (e: IOException) {
            true
        }
    }

    suspend fun logout() {
        val token = session.accessToken
        if (token != null) {
            runCatching { authApi.logout("Bearer $token") }
        }
        session.clear()
    }

    fun isLoggedIn(): Boolean = session.isLoggedIn

    fun currentRole(): String = session.role

    fun currentEmail(): String? = session.email

    private suspend fun startSession(body: AuthResponse) {
        val access = body.accessToken ?: error("Token tidak ditemukan.")
        val refresh = body.refreshToken ?: error("Refresh token tidak ditemukan.")
        val user = body.user ?: error("Data pengguna tidak ditemukan.")
        session.saveTokens(access, refresh, user.id, user.email)
        session.saveRole(fetchRole(user.id))
    }

    /** FR-AUTH-04. Role hanya untuk tampilan; aturan sebenarnya dijaga RLS di database. */
    private suspend fun fetchRole(userId: String): String {
        val response = runCatching { profileApi.getProfile("eq.$userId") }.getOrNull()
        return response?.body()?.firstOrNull()?.role ?: Profile.ROLE_WARGA
    }

    private inline fun <T> safeCall(block: () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: Exception) {
        Result.failure(Exception(e.toUserMessage(), e))
    }
}
