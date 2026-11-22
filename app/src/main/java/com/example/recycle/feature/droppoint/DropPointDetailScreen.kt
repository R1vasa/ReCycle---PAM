package com.example.recycle.feature.droppoint

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Directions
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Hardware
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.WineBar
import androidx.compose.material.icons.rounded.Recycling
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recycle.core.data.model.DropPoint
import com.example.recycle.core.data.remote.SupabaseProvider
import com.example.recycle.feature.droppoint.components.OpenStatus
import com.example.recycle.ui.theme.BackgroundLight
import com.example.recycle.ui.theme.Dimens
import com.example.recycle.ui.theme.OutlineLight
import com.example.recycle.ui.theme.Primary800
import com.example.recycle.ui.theme.ReCycleTheme
import androidx.core.net.toUri

// Pemilik: Anggota 2
// Detail + tombol Maps & Telepon.

data class SampahItem(
    val nama: String,
    val harga: String
)

@Composable
fun DropPointDetailScreen(
    dropPointId: String = "", // Tipe String karena UUID dari Supabase
    onNavigateBack: () -> Unit = {},
    isPengelola: Boolean = false,
    onNavigateToEdit: (String) -> Unit = {},
    viewModel: DropPointViewModel = viewModel(
        factory = DropPointViewModel.provideFactory(
            DropPointRepository(SupabaseProvider.dropPointService)
        )
    ),
    onDropPointClick: (DropPoint) -> Unit = {}
) {
    val context = LocalContext.current
    // Pencocokan UUID (String) dari daftarLokasi di ViewModel
    val selectedDropPoint = viewModel.daftarLokasi.find { it.id == dropPointId }

        when {
            // Kasus A: Sedang loading dari Supabase
            viewModel.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary800)
                }
            }
            // Kasus B: Data tidak ditemukan (misal ID salah atau daftar kosong)
            selectedDropPoint == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Lokasi tidak ditemukan")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onNavigateBack) {
                            Text("Kembali")
                        }
                    }
                }
            }

            // Kasus C: Data ada! Teruskan objeknya ke Content
            else -> {
                var showDeleteDialog by remember { mutableStateOf(false) }

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = {
                            Text(
                                text = "Hapus Lokasi?",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Text(
                                text = "Apakah Anda yakin ingin menghapus '${selectedDropPoint.name}'? Tindakan ini tidak dapat dibatalkan."
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDeleteDialog = false
                                    selectedDropPoint.id?.let { id ->
                                        viewModel.hapusLokasi(id) {
                                            onNavigateBack()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFB3261E)
                                )
                            ) {
                                Text("Hapus", color = Color.White)
                            }
                        },
                        dismissButton = {
                            OutlinedButton(onClick = { showDeleteDialog = false }) {
                                Text("Batal")
                            }
                        }
                    )
                }

                DropPointDetailContent(
                    dropPoint = selectedDropPoint,
                    onBackClick = onNavigateBack,
                    onEditClick = { onNavigateToEdit(dropPointId) },
                    onDeleteClick = { showDeleteDialog = true },
                    onTeleponClick = {
                        if (selectedDropPoint.contactPhone.isNotBlank()) {
                            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                data = "tel:${selectedDropPoint.contactPhone.trim()}".toUri()
                            }
                            context.startActivity(dialIntent)
                        }
                    },
                    onShareClick = {
                        val shareText = """
                            Titik Bank Sampah: ${selectedDropPoint.name}
                            Alamat: ${selectedDropPoint.address}
                            Jam Operasional: ${selectedDropPoint.operatingHours}
                            Kontak: ${selectedDropPoint.contactPhone}
                        """.trimIndent()

                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Bagikan Informasi Lokasi"))
                    },
                    onSampahItemClick = {},
                    onPetunjukArahClick = {
                        val lat = selectedDropPoint.latitude
                        val lng = selectedDropPoint.longitude
                        // Jika ada koordinat lat/lng, buka titik pin koordinatnya.
                        // Jika koordinat kosong/null, fallback mencari berdasarkan nama & alamat.
                        val mapUri = if (lat != null && lng != null) {
                            "geo:$lat,$lng?q=${Uri.encode(selectedDropPoint.name)}".toUri()
                        } else {
                            "geo:0,0?q=${Uri.encode("${selectedDropPoint.name}, ${selectedDropPoint.address}")}".toUri()
                        }
                        val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            // Fallback: jika di emulator/HP tidak ada aplikasi Maps, buka lewat browser
                            val webUri = if (lat != null && lng != null) {
                                "https://www.google.com/maps/search/?api=1&query=$lat,$lng".toUri()
                            } else {
                                "https://www.google.com/maps/search/?api=1&query=${Uri.encode("${selectedDropPoint.name}, ${selectedDropPoint.address}")}".toUri()
                            }
                            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                        }
                    },
                    isOpen = viewModel.isSedangBuka(selectedDropPoint.operatingHours),
                    isPengelola = isPengelola
                )
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropPointDetailContent(
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onTeleponClick: () -> Unit = {},
    onPetunjukArahClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onSampahItemClick: (SampahItem) -> Unit = {},
    isOpen: Boolean,
    dropPoint: DropPoint,
    isPengelola: Boolean = false
) {

    val daftarSampah = listOf(
        SampahItem("Botol PET Bening", "Rp 4.500/kg"),
        SampahItem("Gelas Plastik PP", "Rp 3.000/kg"),
        SampahItem("Kardus", "Rp 2.000/kg"),
        SampahItem("Koran & HVS", "Rp 2.500/kg")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight),
                title = {
                    Text(
                        text = "Detail Lokasi",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    if (isPengelola){
                        IconButton(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit Lokasi",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(onClick = onDeleteClick) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Hapus Lokasi",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomActionBar(
                onTeleponClick = onTeleponClick,
                onPetunjukArahClick = onPetunjukArahClick
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.Space5),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + Dimens.Space2,
                bottom = innerPadding.calculateBottomPadding() + Dimens.Space5
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.Space4)
        ) {
            // 1. Ilustrasi Peta Statis
            item {
                StaticMapIllustration(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            // 2. Judul Lokasi & Status Buka
            item {
                Text(
                    text = dropPoint.name,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                OpenStatus(isOpen = isOpen)
            }

            // 3. Tombol Aksi Cepat (Telepon, Rute, Bagikan)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionTileButton(
                        icon = Icons.Outlined.Phone,
                        label = "Telepon",
                        onClick = onTeleponClick,
                        modifier = Modifier.weight(1f)
                    )
                    ActionTileButton(
                        icon = Icons.Outlined.Directions,
                        label = "Rute",
                        onClick = onPetunjukArahClick,
                        modifier = Modifier.weight(1f)
                    )
                    ActionTileButton(
                        icon = Icons.Outlined.Share,
                        label = "Bagikan",
                        onClick = onShareClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 4. Kartu Informasi Alamat & Telepon
            item {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, color = OutlineLight)
                ) {
                    Column(Modifier.padding(all = 16.dp)) {
                        InfoRow(
                            icon = Icons.Outlined.LocationOn,
                            label = "Alamat",
                            value = dropPoint.address,
                        )
                        Spacer(Modifier.height(Dimens.Space3))
                        InfoRow(
                            icon = Icons.Outlined.Call,
                            label = "Telepon",
                            value = dropPoint.contactPhone
                        )
                    }
                }
            }

            // 5. Kartu Jam Operasional
            item {
                JamOperasionalCard(operatingHours = dropPoint.operatingHours)
            }

            // 6. Kartu Sampah yang Diterima & Daftar Harga
            item {
                SampahDiterimaCard(
                    daftarSampah = daftarSampah,
                    onItemClick = onSampahItemClick
                )
            }

            // 7. Banner Informasi Kuning
            item {
                InfoBannerCard()
            }
        }
    }
}

@Composable
private fun JamOperasionalCard(operatingHours: String) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, color = OutlineLight)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header dengan Ikon Jam
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = Primary800,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Jam operasional",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            // Teks Jam Operasional Nyata dari Database
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF0F5EB))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = operatingHours.ifBlank { "Jam operasional belum tersedia" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Primary800
                )
            }
        }
    }
}

@Composable
private fun SampahDiterimaCard(
    daftarSampah: List<SampahItem>,
    onItemClick: (SampahItem) -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, color = OutlineLight)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Sampah yang diterima",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Badge Kategori Sampah
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KategoriBadge(icon = Icons.Outlined.LocalDrink, label = "Plastik")
                KategoriBadge(icon = Icons.Outlined.Description, label = "Kertas")
                KategoriBadge(icon = Icons.Outlined.Hardware, label = "Logam")
                KategoriBadge(icon = Icons.Outlined.WineBar, label = "Kaca")
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = OutlineLight, thickness = 0.8.dp)

            // Daftar Harga Sampah
            daftarSampah.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.nama,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = item.harga,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (index < daftarSampah.lastIndex) {
                    HorizontalDivider(color = OutlineLight, thickness = 0.8.dp)
                }
            }
        }
    }
}

@Composable
private fun KategoriBadge(
    icon: ImageVector,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF1F6EC))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Primary800,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = Primary800
        )
    }
}

@Composable
private fun InfoBannerCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF9F4DF))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Filled.Lightbulb,
            contentDescription = null,
            tint = Color(0xFF6B5313),
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Text(
            text = "Penimbangan dilakukan langsung di tempat. Saldo dapat dicairkan setiap akhir bulan.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6B5313)
        )
    }
}

@Composable
private fun BottomActionBar(
    onTeleponClick: () -> Unit,
    onPetunjukArahClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column {
            HorizontalDivider(color = OutlineLight, thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.Space5, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onTeleponClick,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, OutlineLight),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Phone,
                        contentDescription = null,
                        tint = Primary800,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Telepon",
                        color = Primary800,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onPetunjukArahClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary800),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Directions,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Petunjuk Arah",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun StaticMapIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE7EDE4)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawRoundRect(
                color = Color(0xFFD6E6CE),
                topLeft = Offset(w * 0.06f, h * 0.12f),
                size = Size(w * 0.2f, h * 0.28f),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFFD6E6CE),
                topLeft = Offset(w * 0.7f, h * 0.12f),
                size = Size(w * 0.24f, h * 0.32f),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
            )

            val riverPath = Path().apply {
                moveTo(0f, h * 0.72f)
                cubicTo(
                    w * 0.35f, h * 0.65f,
                    w * 0.65f, h * 0.85f,
                    w, h * 0.62f
                )
            }
            drawPath(
                path = riverPath,
                color = Color(0xFFD1E0EC),
                style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
            )

            drawLine(
                color = Color.White,
                start = Offset(0f, h * 0.22f),
                end = Offset(w, h * 0.22f),
                strokeWidth = 10.dp.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(0f, h * 0.52f),
                end = Offset(w, h * 0.52f),
                strokeWidth = 12.dp.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(w * 0.35f, 0f),
                end = Offset(w * 0.35f, h),
                strokeWidth = 12.dp.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(w * 0.18f, h * 0.52f),
                end = Offset(w * 0.18f, h),
                strokeWidth = 8.dp.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(w * 0.65f, 0f),
                end = Offset(w * 0.58f, h),
                strokeWidth = 10.dp.toPx()
            )
        }

        CustomMapPin(
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
fun CustomMapPin(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .background(Color.White, CircleShape)
                .padding(4.dp)
                .background(Primary800, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Recycling,
                contentDescription = "Pin Lokasi",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

        Canvas(
            modifier = Modifier
                .size(width = 18.dp, height = 10.dp)
                .offset(y = (-2).dp)
        ) {
            val trianglePath = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2f, size.height)
                close()
            }
            drawPath(path = trianglePath, color = Primary800)
        }
    }
}

@Composable
private fun ActionTileButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.Space4),
        border = BorderStroke(1.dp, color = OutlineLight),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(Dimens.Space6),
                tint = Primary800
            )
            Text(
                text = label,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(Dimens.Space5)
        )
        Spacer(Modifier.width(Dimens.Space3))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

//@Preview(showBackground = true, heightDp = 1100)
//@Composable
//fun PreviewDropPointDetail() {
//    ReCycleTheme {
//        DropPointDetailContent(
//            onBackClick = {},
//            onEditClick = {},
//            onDeleteClick = {}
//        )
//    }
//}