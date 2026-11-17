package com.example.recycle.core.data.remote

import com.example.recycle.core.data.model.Profile
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

// Pemilik: Bersama
// Tabel profiles (rest/v1/profiles). Filter memakai format PostgREST, contoh: "eq.<uuid>".
interface ProfileApiService {
    @GET("profiles")
    suspend fun getProfile(
        @Query("id") idFilter: String,
        @Query("select") select: String = "*"
    ): Response<List<Profile>>

    /** Hanya full_name, phone, address, avatar_url yang boleh diubah (role & email ditolak database). */
    @PATCH("profiles")
    suspend fun updateProfile(
        @Query("id") idFilter: String,
        @Body fields: Map<String, String?>
    ): Response<List<Profile>>
}
