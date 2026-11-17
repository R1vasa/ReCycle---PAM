package com.example.recycle.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recycle.ui.components.ReCycleLogo
import com.example.recycle.ui.theme.Dimens
import com.example.recycle.ui.theme.Primary800
import com.example.recycle.ui.theme.PureWhite
import kotlinx.coroutines.delay

// Pemilik: Bersama
// Cek sesi lalu arahkan ke Login atau Main.
@Composable
fun SplashScreen(
    sessionState: SessionState = SessionState.Checking,
    onSplashFinished: (isLoggedIn: Boolean) -> Unit = {}
){
    // Tampil minimal 2 detik, lalu tunggu sampai pengecekan sesi selesai.
    var minDelayDone by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(2000L)
        minDelayDone = true
    }
    LaunchedEffect(minDelayDone, sessionState) {
        if (minDelayDone && sessionState !is SessionState.Checking) {
            onSplashFinished(sessionState is SessionState.LoggedIn)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary800)
            .padding(Dimens.Space6),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        ReCycleLogo(
            size = 120.dp,
            iconSize = 68.dp,
            cornerRadius = 38.dp,
            containerColor = PureWhite,
            iconColor = Primary800
        )

        Spacer(modifier = Modifier.height(Dimens.Space6))

        Text(
            text = "ReCycle",
            style = MaterialTheme.typography.displaySmall.copy(
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            ),
            color = PureWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.Space2))

        Text(
            text = "Sampahmu, Bernilai untuk Bumi",
            style = MaterialTheme.typography.bodyLarge,
            color = PureWhite.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSplash(){
    SplashScreen()
}