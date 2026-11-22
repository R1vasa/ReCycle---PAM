package com.example.recycle.feature.droppoint

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Hardware
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.WineBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recycle.core.data.model.DropPoint
import com.example.recycle.core.data.remote.SupabaseProvider
import com.example.recycle.core.util.UiState
import com.example.recycle.ui.theme.Dimens
import com.example.recycle.ui.theme.OutlineLight
import com.example.recycle.ui.theme.Primary800
import com.example.recycle.ui.theme.ReCycleTheme
import com.example.recycle.ui.theme.TextPrimary
import com.example.recycle.ui.theme.TextSecondary

// Pemilik: Anggota 2
// Form tambah/edit lokasi (Pengelola).

private val DAFTAR_HARI = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

data class KategoriSampahOption(
    val id: String,
    val label: String,
    val icon: ImageVector
)

private val DAFTAR_KATEGORI_SAMPAH = listOf(
    KategoriSampahOption("plastik", "Plastik", Icons.Outlined.LocalDrink),
    KategoriSampahOption("kertas", "Kertas", Icons.Outlined.Description),
    KategoriSampahOption("logam", "Logam", Icons.Outlined.Hardware),
    KategoriSampahOption("kaca", "Kaca", Icons.Outlined.WineBar),
    KategoriSampahOption("elektronik", "Elektronik", Icons.Outlined.Devices),
    KategoriSampahOption("minyak", "Minyak", Icons.Outlined.Opacity)
)

@Composable
fun DropPointFormScreen(
    dropPointId: String? = null,
    onNavigateBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
    viewModel: DropPointViewModel = viewModel(
        factory = DropPointViewModel.provideFactory(
            DropPointRepository(SupabaseProvider.dropPointService)
        )
    )
){
    val saveState = viewModel.saveState

    // Ambil data lama jika dalam mode edit
    val existingDropPoint = remember(dropPointId, viewModel.daftarLokasi) {
        if (dropPointId != null) {
            viewModel.daftarLokasi.find { it.id == dropPointId }
        } else null
    }

    LaunchedEffect(saveState) {
        if (saveState is UiState.Success) {
            viewModel.resetSaveState()
            onSaveSuccess()
        }
    }

    DropPointFormContent(
        initialDropPoint = existingDropPoint,
        isLoading = saveState is UiState.Loading,
        errorMessage = (saveState as? UiState.Error)?.message,
        onBackClick = onNavigateBack,
        onSaveClick = { dropPointData ->
            if (dropPointId != null) {
                // Mode Edit: update lokasi lama
                viewModel.updateLokasi(dropPointId, dropPointData)
            } else {
                // Mode Tambah: buat lokasi baru
                viewModel.tambahLokasi(dropPointData)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DropPointFormContent(
    initialDropPoint: DropPoint? = null,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onBackClick: () -> Unit = {},
    onSaveClick: (DropPoint) -> Unit = {}
) {
    val isEditMode = initialDropPoint != null

    // 1. Ekstrak Alamat & Kecamatan
    val (initialAlamat, initialKecamatan) = remember(initialDropPoint) {
        val addr = initialDropPoint?.address.orEmpty()
        if (addr.contains(", Kec. ")) {
            val parts = addr.split(", Kec. ")
            Pair(parts[0], parts.getOrNull(1).orEmpty())
        } else {
            Pair(addr, "")
        }
    }

    // 2. Ekstrak Hari Operasional
    val initialHari = remember(initialDropPoint) {
        if (initialDropPoint != null && initialDropPoint.operatingHours.isNotBlank()) {
            val active = DAFTAR_HARI.filter { initialDropPoint.operatingHours.contains(it) }
            if (active.isNotEmpty()) active else listOf("Sen", "Sel", "Rab", "Kam", "Jum")
        } else {
            listOf("Sen", "Sel", "Rab", "Kam", "Jum")
        }
    }

    // 3. Ekstrak Jam Buka & Tutup (misal format: 08.00 - 15.00)
    val (initialJamBuka, initialJamTutup) = remember(initialDropPoint) {
        if (initialDropPoint != null) {
            val regex = """(\d{1,2}[:.]\d{2})\s*-\s*(\d{1,2}[:.]\d{2})""".toRegex()
            val match = regex.find(initialDropPoint.operatingHours)
            if (match != null) {
                Pair(match.groupValues[1], match.groupValues[2])
            } else {
                Pair("", "")
            }
        } else {
            Pair("", "")
        }
    }

    // 4. Ekstrak Sampah yang Diterima
    val initialSampah = remember(initialDropPoint) {
        if (initialDropPoint != null && initialDropPoint.acceptedWaste.isNotBlank()) {
            initialDropPoint.acceptedWaste.split(",").map { it.trim() }
        } else {
            emptyList()
        }
    }

    // State form terisi otomatis jika edit mode
    var namaLokasi by remember(initialDropPoint) { mutableStateOf(initialDropPoint?.name.orEmpty()) }
    var alamatLengkap by remember(initialDropPoint) { mutableStateOf(initialAlamat) }
    var kecamatan by remember(initialDropPoint) { mutableStateOf(initialKecamatan) }
    var nomorTelepon by remember(initialDropPoint) { mutableStateOf(initialDropPoint?.contactPhone.orEmpty()) }

    val selectedHari = remember(initialDropPoint) {
        mutableStateListOf<String>().apply { addAll(initialHari) }
    }

    var jamBuka by remember(initialDropPoint) { mutableStateOf(initialJamBuka) }
    var jamTutup by remember(initialDropPoint) { mutableStateOf(initialJamTutup) }

    val selectedSampah = remember(initialDropPoint) {
        mutableStateListOf<String>().apply { addAll(initialSampah) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Lokasi" else "Tambah Lokasi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
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
                            onClick = onBackClick,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, OutlineLight),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = "Batal",
                                color = Primary800,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = {
                                val hariStr = selectedHari.joinToString(", ")
                                val operatingHours = "$hariStr $jamBuka - $jamTutup".trim()
                                val acceptedWaste = selectedSampah.joinToString(", ")
                                val fullAddress = if (kecamatan.isNotBlank()) {
                                    "$alamatLengkap, Kec. $kecamatan"
                                } else {
                                    alamatLengkap
                                }

                                if (namaLokasi.isBlank()) {
                                    return@Button
                                }

                                if (alamatLengkap.isBlank()) {
                                    return@Button
                                }

                                val dropPointData = DropPoint(
                                    id = initialDropPoint?.id,
                                    name = namaLokasi,
                                    address = fullAddress,
                                    operatingHours = operatingHours,
                                    acceptedWaste = acceptedWaste,
                                    contactPhone = nomorTelepon,
                                    latitude = initialDropPoint?.latitude,
                                    longitude = initialDropPoint?.longitude
                                )
                                onSaveClick(dropPointData)
                            },
                            enabled = !isLoading,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary800),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.Save,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEditMode) "Simpan Perubahan" else "Simpan",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.Space5, vertical = Dimens.Space4),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Nama Lokasi
            CustomFormTextField(
                value = namaLokasi,
                onValueChange = { namaLokasi = it },
                label = "Nama lokasi",
                icon = Icons.Outlined.Storefront
            )

            // 2. Alamat Lengkap
            CustomFormTextField(
                value = alamatLengkap,
                onValueChange = { alamatLengkap = it },
                label = "Alamat lengkap",
                icon = Icons.Outlined.LocationOn
            )

            // 3. Kecamatan
            CustomFormTextField(
                value = kecamatan,
                onValueChange = { kecamatan = it },
                label = "Kecamatan",
                icon = Icons.Outlined.Map
            )

            // 4. Nomor Telepon
            CustomFormTextField(
                value = nomorTelepon,
                onValueChange = { nomorTelepon = it },
                label = "Nomor telepon",
                icon = Icons.Outlined.Phone,
                keyboardType = KeyboardType.Phone
            )

            // 5. Hari Operasional
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionLabel(text = "Hari operasional")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DAFTAR_HARI.forEach { hari ->
                        val isSelected = selectedHari.contains(hari)
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Primary800 else Color.White)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Primary800 else OutlineLight,
                                    shape = CircleShape
                                )
                                .clickable {
                                    if (isSelected) {
                                        selectedHari.remove(hari)
                                    } else {
                                        selectedHari.add(hari)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = hari,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                        }
                    }
                }

                // Jam Buka & Jam Tutup
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TimePickerField(
                        label = "Jam buka",
                        value = jamBuka,
                        onValueChange = { jamBuka = it },
                        modifier = Modifier.weight(1f)
                    )
                    TimePickerField(
                        label = "Jam tutup",
                        value = jamTutup,
                        onValueChange = { jamTutup = it },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 6. Sampah yang Diterima
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionLabel(text = "Sampah yang diterima")

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DAFTAR_KATEGORI_SAMPAH.forEach { option ->
                        val isSelected = selectedSampah.contains(option.label)
                        WasteCategoryChip(
                            option = option,
                            isSelected = isSelected,
                            onToggle = {
                                if (isSelected) {
                                    selectedSampah.remove(option.label)
                                } else {
                                    selectedSampah.add(option.label)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/** Label dengan bintang merah tanda wajib (*) */
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = buildAnnotatedString {
            append(text)
            withStyle(style = SpanStyle(color = Color(0xFFB3261E))) {
                append(" *")
            }
        },
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
    )
}

/** Input form standar dengan rounded 12dp dan leading icon */
@Composable
private fun CustomFormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = buildAnnotatedString {
                    append(label)
                    withStyle(style = SpanStyle(color = Color(0xFFB3261E))) {
                        append(" *")
                    }
                },
                color = TextSecondary,
                fontSize = 15.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary800,
            unfocusedBorderColor = OutlineLight,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

/** Field input jam dengan ikon jam */
@Composable
private fun TimePickerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        },
        placeholder = { Text("08.00", color = OutlineLight) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary800,
            unfocusedBorderColor = OutlineLight,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = modifier
    )
}

/** Chip kategori sampah berbentuk rounded rectangle dengan icon */
@Composable
private fun WasteCategoryChip(
    option: KategoriSampahOption,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFF1F6EC) else Color.White)
            .border(
                width = 1.dp,
                color = if (isSelected) Primary800 else OutlineLight,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onToggle() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = option.icon,
            contentDescription = null,
            tint = if (isSelected) Primary800 else TextSecondary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = option.label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Primary800 else TextPrimary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDropPointFormScreen() {
    ReCycleTheme {
        DropPointFormContent()
    }
}
