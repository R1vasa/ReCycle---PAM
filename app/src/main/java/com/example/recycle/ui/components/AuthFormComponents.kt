package com.example.recycle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.recycle.ui.theme.Dimens
import com.example.recycle.ui.theme.ErrorRed
import com.example.recycle.ui.theme.ErrorSurface
import com.example.recycle.ui.theme.OutlineLight
import com.example.recycle.ui.theme.Primary800
import com.example.recycle.ui.theme.PureWhite
import com.example.recycle.ui.theme.SurfaceGreen
import com.example.recycle.ui.theme.TextPrimary

// Pemilik: Bersama
// Komponen form yang dipakai LoginScreen dan RegisterScreen.

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null

) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        shape = MaterialTheme.shapes.medium,
        colors = authFieldColors(),
        modifier = modifier.fillMaxWidth(),
        leadingIcon = leadingIcon
    )
}

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        enabled = enabled,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        trailingIcon = {
            TextButton(onClick = { visible = !visible }) {
                Text(if (visible) "Sembunyi" else "Lihat", color = Primary800)
            }
        },
        shape = MaterialTheme.shapes.medium,
        colors = authFieldColors(),
        modifier = modifier.fillMaxWidth(),
        leadingIcon = leadingIcon
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = Primary800, contentColor = PureWhite),
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.ButtonHeight)
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = PureWhite, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
        } else {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** Kotak pesan error (merah) atau informasi (hijau). */
@Composable
fun AuthMessage(message: String, isError: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = message,
        color = if (isError) ErrorRed else TextPrimary,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
            .fillMaxWidth()
            .background(if (isError) ErrorSurface else SurfaceGreen, MaterialTheme.shapes.small)
            .padding(Dimens.Space3)
    )
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Primary800,
    unfocusedBorderColor = OutlineLight,
    focusedLabelColor = Primary800,
    cursorColor = Primary800,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent
)
