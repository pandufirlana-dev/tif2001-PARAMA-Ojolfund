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
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.HorizontalDivider
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
import com.ojolfund.app.ui.components.SoftButton
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText
import com.ojolfund.app.ui.theme.OjolfundTheme
import com.ojolfund.app.viewmodel.AuthStatus
import com.ojolfund.app.viewmodel.AuthUiState
import com.ojolfund.app.viewmodel.AuthViewModel

/** Layar Masuk (email + kata sandi) yang terhubung ke `POST /auth/login`. */
@Composable
fun LoginScreen(
    vm: AuthViewModel,
    onBack: () -> Unit,
    onLoggedIn: () -> Unit,
    onRegister: () -> Unit,
    onPhoneLogin: () -> Unit
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(state.status) {
        if (state.status is AuthStatus.LoggedIn) {
            vm.consumeStatus()
            onLoggedIn()
        }
    }

    LoginContent(
        state = state,
        onEmailChange = vm::onEmailChange,
        onPasswordChange = vm::onPasswordChange,
        onSubmit = vm::login,
        onBack = onBack,
        onRegister = onRegister,
        onPhoneLogin = onPhoneLogin
    )
}

@Composable
private fun LoginContent(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    onRegister: () -> Unit,
    onPhoneLogin: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        AuthTopBar(title = "Masuk", onBack = onBack)

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
                "Selamat Datang Kembali",
                style = OjolText.HeadlineLg.copy(fontSize = 28.sp),
                color = OjolColors.TextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                "Masuk untuk melanjutkan pencatatan pemasukan dan biaya narik harianmu.",
                style = OjolText.BodyLg,
                color = OjolColors.TextVariant,
                textAlign = TextAlign.Center
            )

            state.notice?.let { MessageBanner(it, isError = false) }
            state.errorMessage?.let {
                MessageBanner(it, isError = true, modifier = Modifier.testTag("login_error_banner"))
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
                    label = "Email",
                    value = state.email,
                    onValueChange = onEmailChange,
                    placeholder = "nama@email.com",
                    leadingIcon = Icons.Filled.Email,
                    error = state.emailError,
                    keyboardType = KeyboardType.Email,
                    enabled = !state.isLoading,
                    testTag = "login_email"
                )
                OjolTextField(
                    label = "Kata Sandi",
                    value = state.password,
                    onValueChange = onPasswordChange,
                    placeholder = "Masukkan kata sandi",
                    leadingIcon = Icons.Filled.Lock,
                    error = state.passwordError,
                    isPassword = true,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    onImeAction = onSubmit,
                    enabled = !state.isLoading,
                    testTag = "login_password"
                )
                PrimaryButton(
                    text = "Masuk",
                    onClick = onSubmit,
                    loading = state.isLoading,
                    modifier = Modifier.testTag("login_submit")
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Belum punya akun?", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                Text(
                    "Daftar",
                    style = OjolText.LabelLg,
                    color = OjolColors.Primary,
                    modifier = Modifier.clickable(onClick = onRegister).padding(vertical = 6.dp)
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HorizontalDivider(Modifier.weight(1f), color = OjolColors.Border)
                Text("ATAU", style = OjolText.LabelSm, color = OjolColors.TextSecondary)
                HorizontalDivider(Modifier.weight(1f), color = OjolColors.Border)
            }

            SoftButton(
                text = "Masuk dengan Nomor HP (Demo)",
                onClick = onPhoneLogin,
                leadingIcon = Icons.Filled.Sms,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun LoginScreenPreview() {
    OjolfundTheme {
        LoginContent(
            state = AuthUiState(email = "budi@example.com", status = AuthStatus.Error("Email atau kata sandi salah")),
            onEmailChange = {},
            onPasswordChange = {},
            onSubmit = {},
            onBack = {},
            onRegister = {},
            onPhoneLogin = {}
        )
    }
}

