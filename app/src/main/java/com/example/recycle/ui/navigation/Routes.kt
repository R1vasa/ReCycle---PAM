package com.example.recycle.ui.navigation

// Pemilik: Bersama
// Daftar route semua layar.
object Routes{
    val splash = "Splash"
    val login = "Login"
    val register = "Register"
    val main = "Main"

    // Anggota 2 — Direktori Bank Sampah. Setiap layar wajib punya route unik.
    val dropPoints = "drop_points"
    val dropPointDetail = "drop_points/detail/{id}"
    val dropPointForm = "drop_points/form"

    /** id berupa uuid (String), sesuai kolom drop_points.id. */
    fun dropPointDetail(id: String) = "drop_points/detail/$id"
}
