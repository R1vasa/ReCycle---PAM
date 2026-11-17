package com.example.recycle.core.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.recycle.core.data.model.Profile

// Pemilik: Bersama
// Menyimpan sesi login (token, user id, email, role) agar tetap login
// setelah aplikasi ditutup (FR-AUTH-03). Panggil init() di MainActivity.onCreate.
object SessionManager {
    private const val PREFS_NAME = "recycle_session"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_EMAIL = "email"
    private const val KEY_ROLE = "role"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        if (!::prefs.isInitialized) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    val accessToken: String? get() = prefs.getString(KEY_ACCESS_TOKEN, null)
    val refreshToken: String? get() = prefs.getString(KEY_REFRESH_TOKEN, null)
    val userId: String? get() = prefs.getString(KEY_USER_ID, null)
    val email: String? get() = prefs.getString(KEY_EMAIL, null)
    val role: String get() = prefs.getString(KEY_ROLE, null) ?: Profile.ROLE_WARGA

    val isLoggedIn: Boolean get() = refreshToken != null

    val isPengelola: Boolean get() = role == Profile.ROLE_PENGELOLA

    fun saveTokens(accessToken: String, refreshToken: String, userId: String, email: String?) {
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_USER_ID, userId)
            putString(KEY_EMAIL, email)
        }
    }

    fun saveRole(role: String) {
        prefs.edit { putString(KEY_ROLE, role) }
    }

    fun clear() {
        prefs.edit { clear() }
    }
}
