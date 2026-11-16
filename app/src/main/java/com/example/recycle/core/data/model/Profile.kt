package com.example.recycle.core.data.model

import com.google.gson.annotations.SerializedName

// Pemilik: Bersama
// Data class tabel profiles (termasuk role: warga/pengelola).
data class Profile(
    val id: String? = null,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null
)