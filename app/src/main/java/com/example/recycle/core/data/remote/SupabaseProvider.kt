package com.example.recycle.core.data.remote

// Pemilik: Bersama
// Inisialisasi SupabaseClient (Auth, Postgrest, Storage). Hanya anon key.
import com.example.recycle.core.data.local.SessionManager
import com.example.recycle.core.data.model.RefreshTokenRequest
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiKeyInterceptor(
    private val apiKey: String,
    private val useUserToken: Boolean = true
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("apikey", apiKey)
        if (originalRequest.header("Authorization") == null) {
            val token = if (useUserToken) SessionManager.accessToken ?: apiKey else apiKey
            requestBuilder.header("Authorization", "Bearer $token")
        }
        if (useUserToken) {
            requestBuilder.header("Prefer", "return=representation")
        }
        return chain.proceed(requestBuilder.build())
    }
}

/**
 * Access token Supabase berlaku 1 jam. Saat server membalas 401, token diperbarui
 * memakai refresh_token lalu request diulang sekali. Jika gagal, sesi dihapus.
 */
class TokenAuthenticator(
    private val authService: () -> AuthApiService
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.priorResponse != null) return null // sudah dicoba ulang sekali
        val refreshToken = SessionManager.refreshToken ?: return null

        synchronized(this) {
            val current = SessionManager.accessToken
            val sentToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            // Token sudah diperbarui oleh request lain: cukup ulangi dengan token baru.
            if (current != null && current != sentToken) {
                return response.request.newBuilder().header("Authorization", "Bearer $current").build()
            }

            val result = runCatching {
                authService().refreshTokenSync(RefreshTokenRequest(refreshToken)).execute()
            }.getOrNull()
            val body = result?.body()
            val newAccess = body?.accessToken
            val newRefresh = body?.refreshToken
            val user = body?.user
            if (result?.isSuccessful != true || newAccess == null || newRefresh == null || user == null) {
                if (result != null) SessionManager.clear() // refresh_token ditolak server
                return null
            }
            SessionManager.saveTokens(newAccess, newRefresh, user.id, user.email)
            return response.request.newBuilder().header("Authorization", "Bearer $newAccess").build()
        }
    }
}

object SupabaseProvider {
    private const val SUPABASE_URL = "https://mdexyjeyuviifjwqarpz.supabase.co"

    // Akhiri dengan /rest/v1/ sesuai modul
    private const val BASE_URL = "$SUPABASE_URL/rest/v1/"
    private const val AUTH_URL = "$SUPABASE_URL/auth/v1/"
    private const val PUBLISHABLE_KEY = "sb_publishable__xEBhjbCpC2gWYh2x48tPw_FpbrdXzA"

    private val retrofit: Retrofit by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(PUBLISHABLE_KEY))
            .authenticator(TokenAuthenticator { authService })
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val authRetrofit: Retrofit by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(PUBLISHABLE_KEY, useUserToken = false))
            .build()

        Retrofit.Builder()
            .baseUrl(AUTH_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthApiService by lazy {
        authRetrofit.create(AuthApiService::class.java)
    }

    val profileService: ProfileApiService by lazy {
        retrofit.create(ProfileApiService::class.java)
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
