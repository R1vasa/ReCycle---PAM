package com.example.recycle.feature.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recycle.ui.components.ReCycleLogo
import com.example.recycle.ui.theme.Dimens

// Pemilik: Bersama
// FR-AUTH-02.
@Composable
fun LoginScreen(){
    Column(Modifier.padding(horizontal = Dimens.Space5).padding(top = 40.dp).fillMaxWidth()) {
        ReCycleLogo()
        Text(text = "Selamat datang kembali",
            modifier = Modifier.padding(top = 12.dp)
            ,style = MaterialTheme.typography.headlineMedium
        )

        Text(text = "Masuk untuk mengajukan jemput sampah dan memantau setoranmu.",
            modifier = Modifier.padding(top = Dimens.Space1)
        )

        TextField(

        )
    }
}

@Preview
@Composable
fun PreviewLogin(){
    LoginScreen()
}