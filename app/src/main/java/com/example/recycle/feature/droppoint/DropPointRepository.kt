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

    suspend fun addDaftarLokasi(dropPoint: DropPoint): Result<DropPoint> {
        return try {
            val response = apiService.addDropPoints(dropPoint)
            if (response.isSuccessful) {
                // Supabase mengembalikan list yang baru di-insert
                val createdItem = response.body()?.firstOrNull()
                    ?: return Result.failure(Exception("Data kosong dari server"))
                Result.success(createdItem)
            } else {
                Result.failure(Exception("Gagal menambah lokasi: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Di DropPointRepository.kt
    suspend fun updateLokasi(id: String, dropPoint: DropPoint): Result<DropPoint> {
        return try {
            val response = apiService.updateDropPoints("eq.$id", dropPoint)
            if (response.isSuccessful) {
                val data = response.body()?.firstOrNull()
                    ?: return Result.failure(Exception("Data kosong dari server"))
                Result.success(data)
            } else {
                Result.failure(Exception("Gagal update lokasi: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteLokasi(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteDropPoints("eq.$id")
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Gagal menghapus: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}