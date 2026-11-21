package com.example.recycle.feature.droppoint.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Hardware
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Recycling
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.WineBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recycle.R
import com.example.recycle.core.data.model.DropPoint
import com.example.recycle.ui.theme.Dimens
import com.example.recycle.ui.theme.ErrorRed
import com.example.recycle.ui.theme.OutlineLight
import com.example.recycle.ui.theme.Primary800
import com.example.recycle.ui.theme.SurfaceGreen
import com.example.recycle.ui.theme.TextSecondary
import java.util.Locale

// Pemilik: Anggota 2
// Item list: nama, alamat, jam, jarak.

private val IconGray = Color(0xFF73796E)

/**
 * Kartu satu titik pengumpulan dari tabel drop_points.
 *
 * @param distanceKm jarak dari pengguna (DropPointViewModel.hitungJarakKm). Null = tidak ditampilkan.
 * @param isOpen status buka saat ini (DropPointViewModel.isSedangBuka).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DropPointItemCard(
    dropPoint: DropPoint,
    distanceKm: Double?,
    isOpen: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
){
    Column(
        modifier
            .fillMaxWidth()
            .border(width = 2.dp, color = OutlineLight, shape = MaterialTheme.shapes.large)
            .background(Color.White, shape = MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .padding(all = Dimens.Space4)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .background(SurfaceGreen, shape = MaterialTheme.shapes.medium),
                Alignment.Center
            ){
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = Primary800
                )
            }

            Spacer(Modifier.width(Dimens.Space3))

            Column(
                Modifier.weight(1f)
            ) {
                Text(
                    dropPoint.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                OpenStatus(isOpen)
            }

            if (distanceKm != null) {
                Spacer(modifier = Modifier.width(Dimens.Space2))
                Text(
                    formatDistance(distanceKm),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        InfoRow(
            painter = rememberVectorPainter(Icons.Outlined.LocationOn),
            text = dropPoint.address,
            modifier = Modifier.padding(top = Dimens.Space3)
        )

        if (dropPoint.operatingHours.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            InfoRow(
                painter = rememberVectorPainter(Icons.Outlined.Schedule),
                text = dropPoint.operatingHours
            )
        }

        val wasteTypes = parseAcceptedWaste(dropPoint.acceptedWaste)
        if (wasteTypes.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.padding(top = Dimens.Space3),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                wasteTypes.forEach { WasteChip(it) }
            }
        }
    }
}

@Composable
fun OpenStatus(isOpen: Boolean) {
    val color = if (isOpen) Primary800 else ErrorRed
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color = color, shape = CircleShape)
        )
        Text(
            text = if (isOpen) "Buka" else "Tutup",
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun InfoRow(painter: Painter, text: String, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.Top) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = IconGray
        )
        Spacer(Modifier.width(Dimens.Space2))
        Text(text, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun WasteChip(label: String) {
    Row(
        Modifier
            .background(SurfaceGreen, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = Dimens.Space2, vertical = Dimens.Space1),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space1)
    ) {
        Icon(
            painter = wasteIcon(label),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = Primary800
        )
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

/** Ikon per jenis limbah. Tambahkan drawable lain (ic_paper, dst.) di sini bila sudah ada. */
@Composable
private fun wasteIcon(label: String): Painter = when (label.lowercase(Locale.ROOT)) {
    "plastik" -> painterResource(R.drawable.ic_plastic)
    "kertas", "kardus" -> rememberVectorPainter(Icons.Outlined.Description)
    "logam", "besi" -> rememberVectorPainter(Icons.Outlined.Hardware)
    "kaca" -> rememberVectorPainter(Icons.Outlined.WineBar)
    "elektronik" -> rememberVectorPainter(Icons.Outlined.Devices)
    "organik" -> rememberVectorPainter(Icons.Outlined.Eco)
    else -> rememberVectorPainter(Icons.Outlined.Recycling)
}

/** Kolom accepted_waste disimpan sebagai teks dipisah koma, contoh: "Plastik, Kertas, Logam". */
private fun parseAcceptedWaste(raw: String): List<String> =
    raw.split(",", ";")
        .map { part -> part.trim().replaceFirstChar { it.titlecase(Locale.ROOT) } }
        .filter { it.isNotEmpty() }
        .distinct()

/** 0.85 -> "850 m", 1.234 -> "1,2 km". */
fun formatDistance(km: Double): String =
    if (km < 1) "${(km * 1000).toInt()} m"
    else String.format(Locale.forLanguageTag("id-ID"), "%.1f km", km)

@Preview
@Composable
fun PreviewDropPointItemCard(){
    DropPointItemCard(
        dropPoint = DropPoint(
            id = "dp-1",
            name = "Bank Sampah Melati",
            address = "Jl. Melati Raya No. 12, RT 04/RW 02, Cipete Utara, Kebayoran Baru, Jakarta Selatan",
            operatingHours = "Sen–Sab · 08.00–15.00",
            acceptedWaste = "Plastik, Kertas, Logam, Kaca",
            contactPhone = "081234567890"
        ),
        distanceKm = 1.23,
        isOpen = false
    )
}
