package com.example.recycle.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recycle.core.util.UiState
import com.example.recycle.ui.components.AuthMessage
import com.example.recycle.ui.components.AuthTextField
import com.example.recycle.ui.components.PasswordField
import com.example.recycle.ui.components.PrimaryButton
import com.example.recycle.ui.components.ReCycleLogo
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.Icon
import androidx.compose.ui.text.style.TextAlign
import com.example.recycle.ui.theme.Dimens
import com.example.recycle.ui.theme.Primary800
import com.example.recycle.ui.theme.TextSecondary


// Pemilik: Bersama
// FR-AUTH-02.
@Composable
fun LoginScreen(
    authState: UiState<String?> = UiState.Idle,
    onLogin: (email: String, password: String) -> Unit = { _, _ -> },
    onRegisterClick: () -> Unit = {}
){
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val isLoading = authState is UiState.Loading

    Column(
        Modifier
            .padding(horizontal = Dimens.Space5)
            .padding(top = 40.dp, bottom = Dimens.Space6)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        ReCycleLogo()
        Text(text = "Selamat datang kembali",
            modifier = Modifier.padding(top = 12.dp)
            ,style = MaterialTheme.typography.headlineMedium
        )

        Text(text = "Masuk untuk mengajukan jemput sampah dan memantau setoranmu.",
            modifier = Modifier.padding(top = Dimens.Space1),
            color = TextSecondary
        )

        Spacer(Modifier.height(Dimens.Space8))

        AuthTextField(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = "Icon Email"
                )
            },
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardType = KeyboardType.Email,
            enabled = !isLoading
        )

        Spacer(Modifier.height(Dimens.Space4))

        PasswordField(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    "Password"
                )
            },
            value = password,
            onValueChange = { password = it },
            label = "Password",
            imeAction = ImeAction.Done,
            enabled = !isLoading
        )

        if (authState is UiState.Error) {
            AuthMessage(
                message = authState.message,
                isError = true,
                modifier = Modifier.padding(top = Dimens.Space4)
            )
        }

        Spacer(Modifier.height(Dimens.Space3))

        Text(
            text = "lupa kata sandi?",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp).fillMaxWidth(),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.labelLarge,
            color = Primary800
        )

        Spacer(Modifier.height(Dimens.Space3))

        PrimaryButton(
            text = "Masuk",
            onClick = { onLogin(email, password) },
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Space3, bottom = Dimens.Space3)
        ) {
            Text("Belum punya akun?", color = TextSecondary)
            TextButton(onClick = onRegisterClick, enabled = !isLoading) {
                Text("Daftar Sekarang", color = Primary800)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLogin(){
    LoginScreen()
}
