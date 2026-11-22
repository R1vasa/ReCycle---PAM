package com.example.recycle.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Recycling
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recycle.ui.theme.Dimens
import com.example.recycle.ui.theme.OutlineLight
import com.example.recycle.ui.theme.Primary800
import com.example.recycle.ui.theme.ReCycleTheme
import com.example.recycle.ui.theme.TextSecondary

// Pemilik: Bersama (Komponen Navigasi Utama)

/**
 * 4 Tab Utama Aplikasi ReCycle sesuai PRD & Design System
 */
enum class BottomNavTab(
    val label: String,
    val iconActive: ImageVector,
    val iconInactive: ImageVector
) {
    BERANDA(
        label = "Beranda",
        iconActive = Icons.Filled.Home,
        iconInactive = Icons.Outlined.Home
    ),
    SETOR(
        label = "Setor Sampah",
        iconActive = Icons.Rounded.Recycling,
        iconInactive = Icons.Rounded.Recycling
    ),
    LOKASI(
        label = "Lokasi",
        iconActive = Icons.Filled.LocationOn,
        iconInactive = Icons.Outlined.LocationOn
    ),
    PROFIL(
        label = "Profil",
        iconActive = Icons.Filled.Person,
        iconInactive = Icons.Outlined.Person
    )
}

/**
 * Reusable Bottom Navigation Bar yang bisa dipakai di semua layar utama
 */
@Composable
fun ReCycleBottomBar(
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Garis batas pemisah tipis di bagian atas bar
        HorizontalDivider(color = OutlineLight, thickness = 1.dp)

        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp,
            modifier = Modifier.height(Dimens.BottomNavHeight)
        ) {
            BottomNavTab.values().forEach { tab ->
                val isSelected = tab == currentTab

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            onTabSelected(tab)
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) tab.iconActive else tab.iconInactive,
                            contentDescription = tab.label
                        )
                    },
                    label = {
                        Text(
                            text = tab.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        // Warna ikon & teks saat aktif
                        selectedIconColor = Primary800,
                        selectedTextColor = Primary800,
                        // Indikator pill hijau lembut sesuai mockup Figma
                        indicatorColor = Color(0xFFD6E8D5),
                        // Warna saat tidak aktif
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReCycleBottomBar() {
    ReCycleTheme {
        ReCycleBottomBar(
            currentTab = BottomNavTab.LOKASI,
            onTabSelected = {}
        )
    }
}
