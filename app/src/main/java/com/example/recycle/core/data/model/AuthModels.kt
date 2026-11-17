package com.example.recycle.core.data.model

import com.google.gson.annotations.SerializedName

// Pemilik: Bersama
// Request & response untuk Supabase Auth (endpoint /auth/v1/).

data class SignUpRequest(
    val email: String,
    val password: String,
    /** Disimpan Supabase di auth.users.raw_user_meta_data, lalu disalin trigger handle_new_user ke profiles. */
    val data: SignUpMetadata
)

data class SignUpMetadata(
    @SerializedName("full_name")
    val fullName: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String
)

/**
 * Respons login / refresh / signup.
 * Pada signup dengan "Confirm email" aktif, access_token bernilai null dan
 * pengguna harus konfirmasi email dulu sebelum bisa login.
 */
data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("refresh_token")
    val refreshToken: String? = null,
    @SerializedName("expires_in")
    val expiresIn: Long? = null,
    val user: AuthUser? = null,
    // Pada signup tanpa sesi, Supabase mengembalikan objek user langsung di root.
    val id: String? = null,
    val email: String? = null
)

data class AuthUser(
    val id: String,
    val email: String? = null
)
