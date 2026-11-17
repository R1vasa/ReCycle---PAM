package com.example.recycle.core.data.remote

import com.google.gson.JsonParser
import retrofit2.Response
import java.io.IOException

// Pemilik: Bersama
// Mengubah error dari Supabase menjadi pesan yang bisa ditampilkan ke pengguna.

/** Ambil pesan error dari body respons gagal (format Supabase Auth maupun PostgREST). */
fun Response<*>.errorMessage(): String {
    val raw = runCatching { errorBody()?.string() }.getOrNull()
    val message = raw?.let { body ->
        runCatching {
            val json = JsonParser.parseString(body).asJsonObject
            listOf("msg", "error_description", "message", "error")
                .firstNotNullOfOrNull { key -> json.get(key)?.takeIf { it.isJsonPrimitive }?.asString }
        }.getOrNull()
    }
    return translate(message ?: "Terjadi kesalahan (HTTP ${code()}).")
}

/** Pesan untuk exception jaringan atau exception lain. */
fun Throwable.toUserMessage(): String = when (this) {
    is IOException -> "Tidak dapat terhubung ke server. Periksa koneksi internet."
    else -> message ?: "Terjadi kesalahan."
}

private fun translate(message: String): String = when {
    message.contains("Invalid login credentials", ignoreCase = true) -> "Email atau password salah."
    message.contains("already registered", ignoreCase = true) -> "Email sudah terdaftar."
    message.contains("Email not confirmed", ignoreCase = true) -> "Email belum dikonfirmasi. Cek kotak masuk email kamu."
    message.contains("Password should be", ignoreCase = true) -> "Password minimal 6 karakter."
    message.contains("Unable to validate email", ignoreCase = true) -> "Format email tidak valid."
    else -> message
}
