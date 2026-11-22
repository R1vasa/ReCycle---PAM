package com.example.recycle.feature.droppoint

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddLocationAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recycle.core.data.model.DropPoint
import com.example.recycle.core.data.remote.SupabaseProvider
import com.example.recycle.feature.droppoint.components.DropPointItemCard
import com.example.recycle.ui.theme.BackgroundLight
import com.example.recycle.ui.theme.Dimens
import com.example.recycle.ui.theme.Primary800
import com.example.recycle.ui.theme.TextSecondary
import com.example.recycle.ui.components.BottomNavTab
import com.example.recycle.ui.components.ReCycleBottomBar

private val categories = listOf("Buka sekarang", "Plastik", "Kertas", "Logam", "Kaca")

// Pemilik: Anggota 2
// Daftar bank sampah, LazyColumn.
@Composable
fun DropPointListScreen(
    onNavigateToAdd: () -> Unit = {},
    isPengelola: Boolean = false,
    onTabSelected: (BottomNavTab) -> Unit = {},
    viewModel: DropPointViewModel = viewModel(
        factory = DropPointViewModel.provideFactory(
            DropPointRepository(SupabaseProvider.dropPointService)
        )
    ),
    onDropPointClick: (DropPoint) -> Unit = {}
){
    DropPointListContent(
        daftarLokasi = viewModel.daftarLokasi,
        searchQuery = viewModel.searchQuery,
        selectedCategory = viewModel.selectedCategory,
        isLoading = viewModel.isLoading,
        isPengelola = isPengelola,
        errorMessage = viewModel.errorMessage,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onCategorySelected = viewModel::onCategorySelected,
        onRetry = viewModel::fetchDaftarLokasi,
        distanceOf = viewModel::hitungJarakKm,
        isOpen = { viewModel.isSedangBuka(it.operatingHours) },
        onDropPointClick = onDropPointClick,
        onAddClick = onNavigateToAdd,
        onTabSelected = onTabSelected
    )
}

/** Bagian UI tanpa ViewModel, supaya bisa dipakai di Preview. */
@Composable
fun DropPointListContent(
    daftarLokasi: List<DropPoint>,
    searchQuery: String,
    selectedCategory: String,
    isLoading: Boolean,
    isPengelola: Boolean = false,
    errorMessage: String?,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onRetry: () -> Unit,
    distanceOf: (DropPoint) -> Double?,
    isOpen: (DropPoint) -> Boolean,
    onDropPointClick: (DropPoint) -> Unit,
    onAddClick: () -> Unit = {},
    onTabSelected: (BottomNavTab) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            ReCycleBottomBar(
                currentTab = BottomNavTab.LOKASI,
                onTabSelected = onTabSelected
            )
        },
        floatingActionButton = {
            if (isPengelola) {
                ExtendedFloatingActionButton(
                    onClick = onAddClick,
                    containerColor = Primary800,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Outlined.AddLocationAlt, contentDescription = null) },
                    text = { Text("Tambah Lokasi", fontWeight = FontWeight.SemiBold) },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()) // Mencegah tabrakan dengan bottom bar sistem/navigasi
                .background(Color.White)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = BackgroundLight)
                    .statusBarsPadding()
                    .padding(top = 16.dp, bottom = 16.dp)
            ) {
                Text(
                    text = "Lokasi Setor",
                    modifier = Modifier.padding(horizontal = Dimens.Space5),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Bank sampah & titik pengumpulan terdekat",
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .padding(horizontal = Dimens.Space5),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 12.dp, bottom = 12.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.Space5),
                    placeholder = {
                        Text("Cari nama atau kecamatan", color = Color(0xFF5A5D56))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cari",
                            tint = Color(0xFF5A5D56)
                        )
                    },
                    shape = CircleShape,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFECEEE8),
                        unfocusedContainerColor = Color(0xFFECEEE8),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = Dimens.Space5),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { onCategorySelected(category) },
                            label = { Text(category) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White
                            )
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
//                    .background(BackgroundLight)
                ) {
                    when {
                        isLoading && daftarLokasi.isEmpty() -> {
                            CircularProgressIndicator(
                                color = Primary800,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        errorMessage != null && daftarLokasi.isEmpty() -> {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(Dimens.Space6),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    errorMessage,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(Dimens.Space3))
                                OutlinedButton(onClick = onRetry) { Text("Coba lagi") }
                            }
                        }

                        daftarLokasi.isEmpty() -> {
                            Text(
                                text = if (searchQuery.isBlank() && selectedCategory.isBlank())
                                    "Belum ada lokasi bank sampah."
                                else
                                    "Tidak ada lokasi yang cocok.",
                                color = TextSecondary,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(Dimens.Space6)
                            )
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(Dimens.Space4),
                                verticalArrangement = Arrangement.spacedBy(Dimens.Space3)
                            ) {
                                items(daftarLokasi, key = { it.id ?: it.name }) { dropPoint ->
                                    DropPointItemCard(
                                        dropPoint = dropPoint,
                                        distanceKm = distanceOf(dropPoint),
                                        isOpen = isOpen(dropPoint),
                                        onClick = { onDropPointClick(dropPoint) }
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}

@Preview
@Composable
fun PreviewDropPointList(){
    DropPointListContent(
        daftarLokasi = listOf(
            DropPoint(
                id = "dp-1",
                name = "Bank Sampah Melati",
                address = "Jl. Melati Raya No. 12, Cipete Utara, Jakarta Selatan",
                operatingHours = "Sen–Sab · 08.00–15.00",
                acceptedWaste = "Plastik, Kertas, Logam"
            ),
            DropPoint(
                id = "dp-2",
                name = "Pengepul Maju Jaya",
                address = "Jl. Kenanga No. 5, Kebayoran Baru, Jakarta Selatan",
                operatingHours = "Setiap hari · 07.00–17.00",
                acceptedWaste = "Kaca, Elektronik"
            )
        ),
        searchQuery = "",
        selectedCategory = "",
        isLoading = false,
        errorMessage = null,
        onSearchQueryChange = {},
        onCategorySelected = {},
        onRetry = {},
        distanceOf = { 1.2 },
        isOpen = { it.id == "dp-2" },
        onDropPointClick = {},
        onAddClick = {},
        onTabSelected = {}
    )
}
