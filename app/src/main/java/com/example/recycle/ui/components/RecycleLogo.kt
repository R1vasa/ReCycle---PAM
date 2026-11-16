package com.example.recycle.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.recycle.R
import com.example.recycle.ui.theme.Primary800
import com.example.recycle.ui.theme.PureWhite

@Composable
fun ReCycleLogo(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,                     // Ukuran kotak luar
    iconSize: Dp = 28.dp,                 // Ukuran ikon daur ulang di dalamnya
    cornerRadius: Dp = 16.dp,             // Lengkungan sudut kotak
    containerColor: Color = Primary800,   // Warna kotak (Default: Hijau)
    iconColor: Color = PureWhite          // Warna ikon (Default: Putih)
) {
    Box(
        modifier = modifier
            .size(size)
            .background(color = containerColor, shape = RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_recycle_logo),
            contentDescription = "Logo ReCycle",
            modifier = Modifier.size(iconSize),
            colorFilter = ColorFilter.tint(iconColor)
        )
    }
}