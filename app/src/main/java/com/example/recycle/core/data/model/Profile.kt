package com.example.recycle.core.data.model

import com.google.gson.annotations.SerializedName

// Pemilik: Bersama
// Data class tabel profiles (termasuk role: warga/pengelola).
data class Profile(
    val id: String? = null,
    @SerializedName("full_name")
    val name: String = "",
    val email: String = "",
    // Kosong (null) sampai pengguna mengisinya di Edit Profil.
    val phone: String? = null,
    val address: String? = null,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    /** "warga" atau "pengelola". Hanya bisa diubah lewat Supabase Dashboard. */
    val role: String = ROLE_WARGA,
    @SerializedName("created_at")
    val createdAt: String? = null
) {
    companion object {
        const val ROLE_WARGA = "warga"
        const val ROLE_PENGELOLA = "pengelola"
    }
}
