package com.ojolfund.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import com.ojolfund.app.data.formatPhone
import com.ojolfund.app.ui.components.AuthTopBar
import com.ojolfund.app.ui.components.OjolCard
import com.ojolfund.app.ui.components.Pill
import com.ojolfund.app.ui.components.PrimaryButton
import com.ojolfund.app.ui.components.toast
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText
import com.ojolfund.app.viewmodel.AuthViewModel
import com.ojolfund.app.viewmodel.OtpMethod

@Composable
fun LoginScreen(
    vm: AuthViewModel,
    onBack: () -> Unit,
    onOtpSent: () -> Unit,
    onGoogle: () -> Unit,
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        AuthTopBar(title = "Masuk", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Logo besar
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(OjolColors.Aqua),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier.size(58.dp).clip(RoundedCornerShape(18.dp)).background(OjolColors.Primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }

            Text("Masuk atau Daftar Akun", style = OjolText.HeadlineLg.copy(fontSize = 28.sp), color = OjolColors.TextPrimary, textAlign = TextAlign.Center)
            Text(
                "Kelola pemasukan dan biaya operasional narik ojol harian secara aman dan otomatis.",
                style = OjolText.BodyLg,
                color = OjolColors.TextVariant,
                textAlign = TextAlign.Center,
            )

            // Sinkron akun
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Sinkron akun:", style = OjolText.LabelMd, color = OjolColors.TextPrimary)
                Pill("GoRide", bg = OjolColors.GrayBox2, fg = OjolColors.PrimaryDeep, dot = true)
                Pill("GrabBike", bg = OjolColors.GrayBox2, fg = OjolColors.PrimaryDeep, dot = true)
                Pill("Maxim", bg = OjolColors.GrayBox2, fg = OjolColors.PrimaryDeep, dot = true)
            }

            // Kartu form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(OjolColors.GrayBox)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Nomor Handphone", style = OjolText.TitleMd, color = OjolColors.TextPrimary)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.Shield, contentDescription = null, tint = OjolColors.Primary, modifier = Modifier.size(14.dp))
                        Text("WA / SMS Aktif", style = OjolText.LabelSm.copy(fontSize = 11.sp), color = OjolColors.Primary)
                    }
                }

                // Input nomor
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(
                            BorderStroke(1.5.dp, if (state.phoneError != null) OjolColors.ExpenseText else Color.Transparent),
                            RoundedCornerShape(14.dp),
                        )
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(OjolColors.GrayBox)
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        IndonesiaFlag()
                        Text("+62", style = OjolText.TitleMd, color = OjolColors.TextPrimary)
                        Icon(Icons.Filled.ExpandMore, contentDescription = null, tint = OjolColors.TextVariant, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Box(Modifier.width(1.dp).height(30.dp).background(OjolColors.Border))
                    Spacer(Modifier.width(10.dp))
                    BasicTextField(
                        value = formatPhone(state.phone),
                        onValueChange = { vm.onPhoneChange(it) },
                        singleLine = true,
                        textStyle = OjolText.HeadlineMd.copy(fontSize = 22.sp, color = OjolColors.TextPrimary),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Hapus",
                        tint = OjolColors.TextPrimary,
                        modifier = Modifier.size(24.dp).clickable { vm.onPhoneBackspace() },
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = OjolColors.Primary, modifier = Modifier.size(16.dp))
                    Text(
                        state.phoneError ?: "Kode OTP 4-digit akan dikirimkan ke nomor ini.",
                        style = OjolText.BodyMd,
                        color = if (state.phoneError != null) OjolColors.ExpenseText else OjolColors.TextVariant,
                    )
                }

                Text("Metode pengiriman kode OTP:", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MethodOption(
                        title = "WhatsApp",
                        subtitle = "Rekomendasi",
                        selected = state.method == OtpMethod.WHATSAPP,
                        icon = Icons.Filled.Chat,
                        modifier = Modifier.weight(1f),
                        onClick = { vm.setMethod(OtpMethod.WHATSAPP) },
                    )
                    MethodOption(
                        title = "Kirim SMS",
                        subtitle = "Standar",
                        selected = state.method == OtpMethod.SMS,
                        icon = Icons.Filled.Sms,
                        modifier = Modifier.weight(1f),
                        onClick = { vm.setMethod(OtpMethod.SMS) },
                    )
                }

                if (state.isLoading) {
                    Box(Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = OjolColors.Primary, modifier = Modifier.size(28.dp))
                    }
                } else {
                    PrimaryButton(
                        text = "Lanjut Kirim OTP",
                        onClick = { vm.sendOtp(onOtpSent) },
                        leadingIcon = Icons.Filled.Lock,
                        trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                        height = 56.dp,
                    )
                }
            }

            // Divider "ATAU MASUK LEBIH CEPAT"
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f).height(1.dp).background(OjolColors.Border))
                Text(
                    "ATAU MASUK LEBIH CEPAT",
                    style = OjolText.LabelSm.copy(fontSize = 11.sp),
                    color = OjolColors.TextPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                Box(Modifier.weight(1f).height(1.dp).background(OjolColors.Border))
            }

            // Google
            OjolCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                padding = 16.dp,
                onClick = {
                    context.toast("Masuk dengan Google (demo)")
                    onGoogle()
                },
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("G", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4285F4))
                    Spacer(Modifier.width(12.dp))
                    Text("Lanjutkan dengan Google", style = OjolText.TitleMd, color = OjolColors.TextPrimary)
                }
            }

            // Banner keamanan
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(OjolColors.AquaSoft)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    Modifier.size(52.dp).clip(CircleShape).background(OjolColors.Primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text("Keamanan Finansial Driver Terjamin", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                    Text(
                        "Data buku kas, tarikan order harian, & privasi nomor rekening aman berenkripsi bank. Tidak dibagikan ke pihak manapun tanpa izin mitra.",
                        style = OjolText.BodyMd,
                        color = OjolColors.TextVariant,
                    )
                }
            }

            // Kenapa OjolFund
            OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), padding = 14.dp) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Savings, contentDescription = null, tint = OjolColors.PrimaryDeep, modifier = Modifier.size(26.dp))
                        Text("Kenapa OjoLFund?", style = OjolText.TitleLg.copy(fontWeight = FontWeight.Bold), color = OjolColors.TextPrimary)
                    }
                    Pill("100% Gratis", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep)
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    WhyItem(Icons.Filled.Bolt, "Beban Bensin Otomatis", Modifier.weight(1f))
                    WhyItem(Icons.Filled.PieChart, "Rekap Cuan 3 Aplikasi", Modifier.weight(1f))
                }
            }

            Text(
                buildLegalText(),
                style = OjolText.BodyMd,
                color = OjolColors.TextVariant,
                textAlign = TextAlign.Center,
            )
            Row(
                modifier = Modifier.clickable { context.toast("Menghubungi dukungan…") },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Filled.SupportAgent, contentDescription = null, tint = OjolColors.Primary, modifier = Modifier.size(20.dp))
                Text("Butuh bantuan driver? Hubungi Dukungan", style = OjolText.LabelLg, color = OjolColors.Primary)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun buildLegalText(): androidx.compose.ui.text.AnnotatedString =
    androidx.compose.ui.text.buildAnnotatedString {
        append("Dengan masuk, Anda menyetujui ")
        pushStyle(androidx.compose.ui.text.SpanStyle(color = OjolColors.Primary, fontWeight = FontWeight.SemiBold))
        append("Ketentuan Layanan")
        pop()
        append(" & ")
        pushStyle(androidx.compose.ui.text.SpanStyle(color = OjolColors.Primary, fontWeight = FontWeight.SemiBold))
        append("Kebijakan Privasi")
        pop()
        append(" OjoLFund.")
    }

@Composable
private fun IndonesiaFlag() {
    Column(Modifier.size(width = 24.dp, height = 17.dp).clip(RoundedCornerShape(3.dp))) {
        Box(Modifier.weight(1f).fillMaxWidth().background(Color(0xFFE5202E)))
        Box(Modifier.weight(1f).fillMaxWidth().background(Color(0xFFF1F1F1)))
    }
}

@Composable
private fun MethodOption(
    title: String,
    subtitle: String,
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) OjolColors.Aqua else Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier.size(38.dp).clip(CircleShape).background(if (selected) OjolColors.Primary else OjolColors.GrayBox),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) Color.White else OjolColors.TextVariant, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = OjolText.LabelLg.copy(fontWeight = FontWeight.Bold), color = OjolColors.TextPrimary, maxLines = 1)
            Text(subtitle, style = OjolText.LabelSm.copy(fontSize = 11.sp), color = if (selected) OjolColors.PrimaryDeep else OjolColors.TextVariant)
        }
        if (selected) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = OjolColors.Primary, modifier = Modifier.size(22.dp))
        } else {
            Box(Modifier.size(20.dp).border(1.5.dp, Color(0xFFBDC9C7), CircleShape))
        }
    }
}

@Composable
private fun WhyItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(OjolColors.GrayBox)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(icon, contentDescription = null, tint = OjolColors.PrimaryDeep, modifier = Modifier.size(20.dp))
        Text(text, style = OjolText.BodyMd, color = OjolColors.TextPrimary)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        vm = AuthViewModel(),
        onBack = {},
        onOtpSent = {},
        onGoogle = {},
    )
}