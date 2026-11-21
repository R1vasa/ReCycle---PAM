package com.example.recycle.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.recycle.R

val AppFontFamily = FontFamily(
    Font(
        R.font.roboto_reguler,
        weight = FontWeight.Normal
    ),
    Font(
        R.font.roboto_medium,
        weight = FontWeight.Medium
    ),
    Font(
        R.font.roboto_semibold,
        weight = FontWeight.SemiBold
    ),
    Font(
        R.font.roboto_bold,
        weight = FontWeight.Bold
    )
)

val ReCycleTypography = Typography(
    // Display: 32 px, Bold (Headline besar / hero)
    displaySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        color = TextPrimary
    ),
    // Headline: 24 px, Bold (Judul halaman utama)
    headlineMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        color = TextPrimary
    ),
    // Title Large: 20 px, Medium (Judul section atau detail)
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        color = TextPrimary
    ),
    // Title Medium: 16 px, Medium (Judul card)
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = TextPrimary
    ),
    // Body Large: 16 px, Regular (Isi utama dan input)
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = TextPrimary
    ),
    // Body Medium: 14 px, Regular (Deskripsi dan informasi pendukung)
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = TextPrimary
    ),
    // Label Large: 14 px, Medium (Tombol dan label penting)
    labelLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Label Small: 12 px, Medium (Chip, metadata, helper text)
    labelSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)