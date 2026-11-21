package com.example.recycle.core.data.remote

import com.example.recycle.core.data.model.DropPoint
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface DropPointApiService {
    @GET("drop_points")
    suspend fun getDropPoints(
        @Query("select") select: String = "*"
    ): Response<List<DropPoint>>

    @POST("drop_points")
    suspend fun addDropPoints(
        @Body category: DropPoint
    ): Response<List<DropPoint>>

    @PATCH("drop_points")
    suspend fun updateDropPoints(
        @Query("id") idFilter: String,
        @Body category: DropPoint
    ): Response<List<DropPoint>>

    @DELETE("drop_points")
    suspend fun deleteDropPoints(
        @Query("id") idFilter: String
    ): Response<Unit>

}