package com.example.recycle.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val ReCycleShapes = Shapes(
    // Radius Small: 8 px (Chip kecil dan elemen ringkas)
    small = RoundedCornerShape(8.dp),
    // Radius Medium: 12 px (Input dan tombol tertentu)
    medium = RoundedCornerShape(12.dp),
    // Radius Large: 16 px (Card utama)
    large = RoundedCornerShape(16.dp),
    // Radius Extra Large: 28 px (Dialog dan sheet)
    extraLarge = RoundedCornerShape(28.dp)
)