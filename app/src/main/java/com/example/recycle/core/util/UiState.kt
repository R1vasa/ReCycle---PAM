package com.example.recycle.core.util

// Pemilik: Bersama
// Sealed class Loading / Success / Error untuk semua ViewModel.
sealed interface UiState<out T> {
    /** Belum ada aksi (misalnya form baru dibuka). */
    data object Idle : UiState<Nothing>

    /** Sedang memuat / mengirim data ke cloud. Tampilkan CircularProgressIndicator. */
    data object Loading : UiState<Nothing>

    data class Success<T>(val data: T) : UiState<T>

    data class Error(val message: String) : UiState<Nothing>
}
