package com.example.recycle.feature.auth

import android.text.style.IconMarginSpan
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
import com.example.recycle.ui.theme.Dimens
import com.example.recycle.ui.theme.Primary800
import com.example.recycle.ui.theme.TextSecondary
import java.nio.file.WatchEvent

// Pemilik: Bersama
// FR-AUTH-01.
@Composable
fun RegisterScreen(
    authState: UiState<String?> = UiState.Idle,
    onRegister: (fullName: String, email: String, password: String) -> Unit = { _, _, _ -> },
    onLoginClick: () -> Unit = {}
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    val isLoading = authState is UiState.Loading

    Column(
        Modifier
            .padding(horizontal = Dimens.Space5)
            .padding(top = Dimens.Space5, bottom = Dimens.Space6)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        IconButton(
            onClick = {/*TODO*/},
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent),
            modifier = Modifier
                .size(28.dp)
                .offset(x = (-4).dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                "Back",
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(Dimens.Space5))

        Text(
            text = "Buat akun ReCycle",
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Gratis. Mulai pilah, setor, dan pantau nilai sampahmu.",
            modifier = Modifier.padding(top = Dimens.Space1),
            color = TextSecondary
        )

        Spacer(Modifier.height(Dimens.Space8))

        AuthTextField(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    "Nama lengkap"
                )
            },
            value = fullName,
            onValueChange = { fullName = it },
            label = "Nama Lengkap",
            enabled = !isLoading
        )

        Spacer(Modifier.height(Dimens.Space4))
        AuthTextField(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Mail,
                    "Email"
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
            label = "Kata Sandi",
            imeAction = ImeAction.Next,
            enabled = !isLoading
        )
        Text(
            "Minimal 8 karakter, gabungan huruf dan angka",
            Modifier.padding(horizontal = Dimens.Space4),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )

        when (authState) {
            is UiState.Error -> AuthMessage(authState.message, isError = true, modifier = Modifier.padding(top = Dimens.Space4))
            is UiState.Success -> authState.data?.let {
                AuthMessage(it, isError = false, modifier = Modifier.padding(top = Dimens.Space4))
            }
            else -> Unit
        }

        Spacer(Modifier.height(Dimens.Space6))

        PrimaryButton(
            text = "Daftar",
            onClick = { onRegister(fullName, email, password)},
            isLoading = isLoading
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Space3)
        ) {
            Text("Sudah punya akun?", color = TextSecondary)
            TextButton(onClick = onLoginClick, enabled = !isLoading) {
                Text("Masuk", color = Primary800)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister() {
    RegisterScreen()
}
