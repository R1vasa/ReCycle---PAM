package com.example.recycle.feature.droppoint

import com.example.recycle.core.data.model.DropPoint
import com.example.recycle.core.data.remote.DropPointApiService

// Pemilik: Anggota 2
// CRUD drop_points.
class DropPointRepository(
    private val apiService: DropPointApiService
){
    suspend fun getDaftarLokasi(): Result<List<DropPoint>> {
        return try {
            val response = apiService.getDropPoints()
            if (response.isSuccessful) {
                val data = response.body() ?: emptyList()
                Result.success(data)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}