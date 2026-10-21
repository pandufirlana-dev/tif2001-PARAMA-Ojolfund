package com.ojolfund.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojolfund.app.ui.components.AuthHeroLogo
import com.ojolfund.app.ui.components.AuthTopBar
import com.ojolfund.app.ui.components.MessageBanner
import com.ojolfund.app.ui.components.OjolTextField
import com.ojolfund.app.ui.components.PrimaryButton
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText
import com.ojolfund.app.ui.theme.OjolfundTheme
import com.ojolfund.app.validation.AuthValidator
import com.ojolfund.app.viewmodel.AuthStatus
import com.ojolfund.app.viewmodel.AuthUiState
import com.ojolfund.app.viewmodel.AuthViewModel

/** Layar Daftar (nama, email, kata sandi) yang terhubung ke `POST /auth/register`. */
@Composable
fun RegisterScreen(vm: AuthViewModel, onBack: () -> Unit, onRegistered: () -> Unit, onLogin: () -> Unit) {
    val state by vm.state.collectAsState()

    LaunchedEffect(state.status) {
        if (state.status is AuthStatus.Registered) {
            vm.consumeStatus()
            onRegistered()
        }
    }

    RegisterContent(
        state = state,
        onNameChange = vm::onNameChange,
        onEmailChange = vm::onEmailChange,
        onPasswordChange = vm::onPasswordChange,
        onConfirmChange = vm::onConfirmPasswordChange,
        onSubmit = vm::register,
        onBack = onBack,
        onLogin = onLogin
    )
}

@Composable
private fun RegisterContent(
    state: AuthUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    onLogin: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        AuthTopBar(title = "Daftar", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AuthHeroLogo()
            Text(
                "Buat Akun Baru",
                style = OjolText.HeadlineLg.copy(fontSize = 28.sp),
                color = OjolColors.TextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                "Daftar gratis dan mulai kelola keuangan narik ojol harianmu.",
                style = OjolText.BodyLg,
                color = OjolColors.TextVariant,
                textAlign = TextAlign.Center
            )

            state.errorMessage?.let {
                MessageBanner(it, isError = true, modifier = Modifier.testTag("register_error_banner"))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(OjolColors.GrayBox)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OjolTextField(
                    label = "Nama Lengkap",
                    value = state.name,
                    onValueChange = onNameChange,
                    placeholder = "Nama kamu",
                    leadingIcon = Icons.Filled.Person,
                    error = state.nameError,
                    capitalization = KeyboardCapitalization.Words,
                    enabled = !state.isLoading,
                    testTag = "register_name"
                )
                OjolTextField(
                    label = "Email",
                    value = state.email,
                    onValueChange = onEmailChange,
                    placeholder = "nama@email.com",
                    leadingIcon = Icons.Filled.Email,
                    error = state.emailError,
                    keyboardType = KeyboardType.Email,
                    enabled = !state.isLoading,
                    testTag = "register_email"
                )
                OjolTextField(
                    label = "Kata Sandi",
                    value = state.password,
                    onValueChange = onPasswordChange,
                    placeholder = "Minimal ${AuthValidator.MIN_PASSWORD_LENGTH} karakter",
                    leadingIcon = Icons.Filled.Lock,
                    error = state.passwordError,
                    isPassword = true,
                    keyboardType = KeyboardType.Password,
                    enabled = !state.isLoading,
                    testTag = "register_password"
                )
                OjolTextField(
                    label = "Konfirmasi Kata Sandi",
                    value = state.confirmPassword,
                    onValueChange = onConfirmChange,
                    placeholder = "Ulangi kata sandi",
                    leadingIcon = Icons.Filled.Lock,
                    error = state.confirmError,
                    isPassword = true,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    onImeAction = onSubmit,
                    enabled = !state.isLoading,
                    testTag = "register_confirm"
                )
                PrimaryButton(
                    text = "Daftar",
                    onClick = onSubmit,
                    loading = state.isLoading,
                    modifier = Modifier.testTag("register_submit")
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Sudah punya akun?", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                Text(
                    "Masuk",
                    style = OjolText.LabelLg,
                    color = OjolColors.Primary,
                    modifier = Modifier.clickable(onClick = onLogin).padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun RegisterScreenPreview() {
    OjolfundTheme {
        RegisterContent(
            state = AuthUiState(name = "Budi", email = "budi", passwordError = "Kata sandi minimal 8 karakter"),
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmChange = {},
            onSubmit = {},
            onBack = {},
            onLogin = {}
        )
    }
}
