package com.example.recycle.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.recycle.feature.auth.SessionState
import com.example.recycle.ui.theme.Dimens
import com.example.recycle.ui.theme.TextSecondary

// Pemilik: Bersama
// Scaffold + Bottom Navigation Bar (Beranda, Setor Sampah, Lokasi, Profil).
// SEMENTARA: baru menampilkan info sesi + Logout untuk menguji autentikasi.
// Ganti dengan Scaffold + Bottom Navigation saat tab mulai dikerjakan.
@Composable
fun MainScreen(
    session: SessionState.LoggedIn,
    onLogout: () -> Unit,
    onOpenDropPoints: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.Space5),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login berhasil", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(Dimens.Space2))
        Text(session.email ?: "-", color = TextSecondary)
        Text("Peran: ${session.role}", color = TextSecondary)
        Spacer(Modifier.height(Dimens.Space6))
        // SEMENTARA: pintu masuk ke layar Lokasi sampai bottom navigation dibuat.
        OutlinedButton(onClick = onOpenDropPoints) { Text("Lihat Lokasi Setor") }
        Spacer(Modifier.height(Dimens.Space2))
        OutlinedButton(onClick = onLogout) { Text("Logout") }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    MainScreen(SessionState.LoggedIn("warga@contoh.com", "warga"), onLogout = {})
}
