package com.example.recycle.core.data.model

import com.google.gson.annotations.SerializedName

// Pemilik: Anggota 2
// Data class tabel drop_points. Juga dibaca Anggota 1 untuk dropdown.
data class DropPoint(
    val id: Int? = null,
    val name: String = "",
    val address: String = "",
    @SerializedName("operating_hours")
    val operatingHours: String = "",
    @SerializedName("acceptedWaste")
    val acceptedWaste: String = "",
    @SerializedName("contact_phone")
    val contactPhone: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerializedName("created_at")
    val createdAt: String? = null
)