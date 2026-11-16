package com.example.recycle.core.data.remote

// Pemilik: Bersama
// Inisialisasi SupabaseClient (Auth, Postgrest, Storage). Hanya anon key.
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("apikey", apiKey)
            .header("Authorization", "Bearer $apiKey")
            .header("Prefer", "return=representation")
        return chain.proceed(requestBuilder.build())
    }
}

object SupabaseProvider {
    // Akhiri dengan /rest/v1/ sesuai modul
    private const val BASE_URL = "https://mdexyjeyuviifjwqarpz.supabase.co/rest/v1/"
    private const val PUBLISHABLE_KEY = "sb_publishable__xEBhjbCpC2gWYh2x48tPw_FpbrdXzA"

    private val retrofit: Retrofit by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(PUBLISHABLE_KEY))
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val pickupService: PickUpApiService by lazy {
        retrofit.create(PickUpApiService::class.java)
    }

    val dropPointService: DropPointApiService by lazy {
        retrofit.create(DropPointApiService::class.java)
    }

    val catalogService: CatalogApiService by lazy {
        retrofit.create(CatalogApiService::class.java)
    }

    val educationService: EducationApiService by lazy {
        retrofit.create(EducationApiService::class.java)
    }
}