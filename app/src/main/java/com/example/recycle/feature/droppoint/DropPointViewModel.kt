package com.example.recycle.feature.droppoint

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.recycle.core.data.model.DropPoint
import com.example.recycle.feature.droppoint.DropPointRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class DropPointViewModel(
    private val repository: DropPointRepository
) : ViewModel() {

    // Menyimpan data asli dari API/Repository
    private var semuaLokasi: List<DropPoint> = emptyList()

    // State daftar lokasi yang ditampilkan ke UI
    var daftarLokasi by mutableStateOf<List<DropPoint>>(emptyList())
        private set

    // State pencarian & kategori filter
    var searchQuery by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf("")
        private set

    // Koordinat lokasi pengguna saat ini (bisa diupdate dari GPS/FusedLocationProvider)
    var userLatitude by mutableStateOf<Double?>(null)
        private set
    var userLongitude by mutableStateOf<Double?>(null)
        private set

    // State Loading & Error
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchDaftarLokasi()
    }

    fun fetchDaftarLokasi() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            repository.getDaftarLokasi()
                .onSuccess { data ->
                    semuaLokasi = data
                    terapkanFilterDanUrutkan()
                }
                .onFailure { exception ->
                    errorMessage = exception.message ?: "Gagal memuat data lokasi"
                }

            isLoading = false
        }
    }

    // Panggil fungsi ini jika berhasil mendapatkan lokasi GPS user
    fun updateUserLocation(lat: Double, lng: Double) {
        userLatitude = lat
        userLongitude = lng
        terapkanFilterDanUrutkan()
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        terapkanFilterDanUrutkan()
    }

    fun onCategorySelected(category: String) {
        selectedCategory = if (selectedCategory == category) "" else category
        terapkanFilterDanUrutkan()
    }

    private fun terapkanFilterDanUrutkan() {
        val filtered = semuaLokasi.filter { dropPoint ->
            // 1. Filter berdasarkan nama atau alamat
            val cocokPencarian = searchQuery.isBlank() ||
                    dropPoint.name.contains(searchQuery, ignoreCase = true) ||
                    dropPoint.address.contains(searchQuery, ignoreCase = true)

            // 2. Filter berdasarkan chip kategori
            val cocokKategori = when (selectedCategory) {
                "" -> true
                "Buka sekarang" -> isSedangBuka(dropPoint.operatingHours)
                else -> dropPoint.acceptedWaste.contains(selectedCategory, ignoreCase = true)
            }

            cocokPencarian && cocokKategori
        }

        // 3. Urutkan berdasarkan jarak terdekat jika koordinat user tersedia
        daftarLokasi = if (userLatitude != null && userLongitude != null) {
            filtered.sortedBy { hitungJarakKm(it) ?: Double.MAX_VALUE }
        } else {
            filtered
        }
    }

    // Fungsi pembantu untuk menghitung jarak (dalam km) antara user & drop point
    fun hitungJarakKm(dropPoint: DropPoint): Double? {
        val uLat = userLatitude ?: return null
        val uLng = userLongitude ?: return null
        val dLat = dropPoint.latitude ?: return null
        val dLng = dropPoint.longitude ?: return null

        val hasil = FloatArray(1)
        Location.distanceBetween(uLat, uLng, dLat, dLng, hasil)
        return (hasil[0] / 1000.0) // Konversi meter ke kilometer
    }

    // Fungsi pembantu sederhana untuk mengecek jam operasional (format misal: "08.00–15.00" atau "08:00-17:00")
    fun isSedangBuka(operatingHours: String): Boolean {
        return try {
            // Mengekstrak jam & menit (misal dari "08.00–15.00" atau "08:00 - 17:00")
            val regex = Regex("""(\d{2})[.:](\d{2})\s*[-–]\s*(\d{2})[.:](\d{2})""")
            val match = regex.find(operatingHours) ?: return false

            val jamBuka = match.groupValues[1].toInt()
            val menitBuka = match.groupValues[2].toInt()
            val jamTutup = match.groupValues[3].toInt()
            val menitTutup = match.groupValues[4].toInt()

            // Konversi waktu ke total menit sejak pukul 00:00 agar mudah dibandingkan
            val totalMenitBuka = jamBuka * 60 + menitBuka
            val totalMenitTutup = jamTutup * 60 + menitTutup

            val sekarang = Calendar.getInstance()
            val totalMenitSekarang = sekarang.get(Calendar.HOUR_OF_DAY) * 60 + sekarang.get(Calendar.MINUTE)

            totalMenitSekarang in totalMenitBuka until totalMenitTutup
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        fun provideFactory(repository: DropPointRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    DropPointViewModel(repository)
                }
            }
    }
}