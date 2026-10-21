package com.ojolfund.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojolfund.app.data.formatPhone
import com.ojolfund.app.ui.components.AuthTopBar
import com.ojolfund.app.ui.components.Pill
import com.ojolfund.app.ui.components.PrimaryButton
import com.ojolfund.app.ui.components.toast
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText
import com.ojolfund.app.viewmodel.OtpAuthViewModel
import com.ojolfund.app.viewmodel.OtpMethod
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(vm: OtpAuthViewModel, onBack: () -> Unit, onVerified: () -> Unit) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    // Hitung mundur kirim ulang
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            vm.tick()
        }
    }

    val methodName = if (state.method == OtpMethod.WHATSAPP) "WhatsApp" else "SMS"
    val timer = "%02d:%02d".format(state.secondsLeft / 60, state.secondsLeft % 60)

    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        AuthTopBar(title = "Verifikasi Otp", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Pill(
                "VERIFIKASI KEAMANAN",
                bg = OjolColors.Aqua,
                fg = OjolColors.PrimaryDeep,
                icon = Icons.Filled.Shield,
                textSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                "Masukkan Kode OTP",
                style = OjolText.HeadlineLg.copy(fontSize = 30.sp),
                color = OjolColors.TextPrimary
            )

            Text(
                buildAnnotatedString {
                    append("Kode verifikasi 6 digit telah dikirimkan via ")
                    withStyle(SpanStyle(color = OjolColors.PrimaryDeep, fontWeight = FontWeight.Bold)) {
                        append(methodName)
                    }
                    append(" ke nomor")
                },
                style = OjolText.BodyLg,
                color = OjolColors.TextVariant,
                textAlign = TextAlign.Center
            )

            // Chip nomor
            Surface(shape = RoundedCornerShape(16.dp), color = Color.White, shadowElevation = 2.dp) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        Modifier.size(38.dp).clip(CircleShape).background(OjolColors.GrayBox2),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Chat,
                            contentDescription = null,
                            tint = OjolColors.PrimaryDeep,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        "+62 " + formatPhone(state.phone).replace(' ', '-'),
                        style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                        color = OjolColors.TextPrimary
                    )
                    Text(
                        "Ubah",
                        style = OjolText.LabelLg.copy(textDecoration = TextDecoration.Underline),
                        color = OjolColors.PrimaryDeep,
                        modifier = Modifier.clickable(onClick = onBack)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // Kotak OTP
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 0 until 6) {
                    val char = state.otp.getOrNull(i)
                    val active = i == state.otp.length
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (char != null || active) Color.White else OjolColors.GrayBox),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            char != null -> Text(
                                char.toString(),
                                style = OjolText.HeadlineLg.copy(fontSize = 28.sp),
                                color = OjolColors.PrimaryDeep
                            )
                            active -> Text(
                                "|",
                                style = OjolText.HeadlineLg.copy(fontSize = 28.sp),
                                color = OjolColors.Primary
                            )
                        }
                    }
                }
            }

            if (state.otpError != null) {
                Text(state.otpError!!, style = OjolText.LabelMd, color = OjolColors.ExpenseText)
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = OjolColors.TextVariant,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    buildAnnotatedString {
                        append("Kirim ulang kode dalam ")
                        withStyle(SpanStyle(color = OjolColors.PrimaryDeep, fontWeight = FontWeight.Bold)) {
                            append(timer)
                        }
                    },
                    style = OjolText.BodyMd,
                    color = OjolColors.TextVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Kirim Ulang via WhatsApp",
                    style = OjolText.LabelLg,
                    color = if (state.secondsLeft == 0) OjolColors.PrimaryDeep else OjolColors.TextSecondary,
                    modifier = Modifier.clickable {
                        if (state.secondsLeft == 0) {
                            vm.setMethod(OtpMethod.WHATSAPP)
                            vm.resend()
                            context.toast("Kode OTP dikirim ulang via WhatsApp")
                        } else {
                            context.toast("Tunggu hitung mundur selesai")
                        }
                    }
                )
                Box(Modifier.size(5.dp).clip(CircleShape).background(OjolColors.TextSecondary))
                Text(
                    "Kirim lewat SMS biasa",
                    style = OjolText.LabelLg,
                    color = OjolColors.PrimaryDeep,
                    modifier = Modifier.clickable {
                        vm.setMethod(OtpMethod.SMS)
                        vm.resend()
                        context.toast("Kode OTP dikirim via SMS")
                    }
                )
            }

            // Catatan keamanan
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(OjolColors.GrayBox)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    Modifier.size(34.dp).clip(CircleShape).background(OjolColors.GrayBox2),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Shield,
                        contentDescription = null,
                        tint = OjolColors.PrimaryDeep,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = OjolColors.TextPrimary)) {
                            append("Jaga Keamanan Akun: ")
                        }
                        append("Jangan pernah bagikan kode OTP ini ke siapapun, termasuk yang mengatasnamakan ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = OjolColors.PrimaryDeep)) {
                            append("OjoLFund")
                        }
                        append(".")
                    },
                    style = OjolText.BodyMd,
                    color = OjolColors.TextVariant,
                    modifier = Modifier.weight(1f)
                )
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OjolColors.Primary, modifier = Modifier.size(28.dp))
                }
            } else {
                PrimaryButton(
                    text = "Verifikasi & Masuk",
                    onClick = { vm.verifyOtp(onVerified) },
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                    color = OjolColors.PrimaryDark,
                    height = 56.dp
                )
            }

            // Keypad
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(OjolColors.GrayBox)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(listOf('1', '2', '3'), listOf('4', '5', '6'), listOf('7', '8', '9')).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { d ->
                            KeyButton(Modifier.weight(1f), onClick = { vm.onOtpDigit(d) }) {
                                Text(d.toString(), style = OjolText.HeadlineLg, color = OjolColors.TextPrimary)
                            }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KeyButton(Modifier.weight(1f), bg = Color.Transparent, onClick = {
                        context.toast("Biometrik belum tersedia (demo)")
                    }) {
                        Icon(
                            Icons.Filled.Fingerprint,
                            contentDescription = "Biometrik",
                            tint = OjolColors.TextSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    KeyButton(Modifier.weight(1f), onClick = { vm.onOtpDigit('0') }) {
                        Text("0", style = OjolText.HeadlineLg, color = OjolColors.TextPrimary)
                    }
                    KeyButton(Modifier.weight(1f), bg = OjolColors.GrayBox2, onClick = { vm.onOtpBackspace() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = "Hapus",
                            tint = OjolColors.TextPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun KeyButton(
    modifier: Modifier = Modifier,
    bg: Color = Color.White,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}
