package com.example.recycle.core.data.remote

import com.example.recycle.core.data.model.AuthResponse
import com.example.recycle.core.data.model.LoginRequest
import com.example.recycle.core.data.model.RefreshTokenRequest
import com.example.recycle.core.data.model.SignUpRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Pemilik: Bersama
// Endpoint Supabase Auth. Base URL: https://<project>.supabase.co/auth/v1/
interface AuthApiService {
    @POST("signup")
    suspend fun signUp(@Body body: SignUpRequest): Response<AuthResponse>

    @POST("token?grant_type=password")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST("token?grant_type=refresh_token")
    suspend fun refreshToken(@Body body: RefreshTokenRequest): Response<AuthResponse>

    /** Versi sinkron, dipakai TokenAuthenticator (OkHttp tidak mendukung suspend). */
    @POST("token?grant_type=refresh_token")
    fun refreshTokenSync(@Body body: RefreshTokenRequest): Call<AuthResponse>

    @POST("logout")
    suspend fun logout(@Header("Authorization") bearer: String): Response<Unit>
}
