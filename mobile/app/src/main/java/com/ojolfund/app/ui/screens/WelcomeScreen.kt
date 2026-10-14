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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojolfund.app.ui.components.AuthTopBar
import com.ojolfund.app.ui.components.IconBox
import com.ojolfund.app.ui.components.OjolCard
import com.ojolfund.app.ui.components.OjolLogo
import com.ojolfund.app.ui.components.Pill
import com.ojolfund.app.ui.components.PrimaryButton
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText

@Composable
fun WelcomeScreen(
    onStart: () -> Unit,
    onLogin: () -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit,
) {
    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        AuthTopBar(title = "Masuk", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Brand row
            Row(verticalAlignment = Alignment.CenterVertically) {
                OjolLogo(size = 56.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("OjoLFund", style = OjolText.HeadlineMd, color = OjolColors.TextPrimary)
                        Box(Modifier.size(7.dp).clip(CircleShape).background(OjolColors.Primary))
                    }
                    Text("Buku Kas Mitra Driver", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                }
                Text(
                    "Lewati",
                    style = OjolText.LabelLg,
                    color = OjolColors.TextVariant,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(OjolColors.GrayBox2)
                        .clickable(onClick = onSkip)
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                )
            }

            HeroPreviewCard()
            BannerCard()

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Keuangan Ojol Lebih Terkontrol",
                    style = OjolText.HeadlineLg.copy(fontSize = 26.sp, lineHeight = 32.sp),
                    color = OjolColors.TextPrimary,
                )
                Text(
                    "Catat pemasukan dan pengeluaran, hitung untung bersih riil otomatis, dan pantau biaya bensin dalam satu aplikasi.",
                    style = OjolText.BodyLg,
                    color = OjolColors.TextVariant,
                )
            }

            FeatureRow(Icons.Filled.Bolt, "Kalkulasi Bersih Kilat", "Komisi aplikator dipotong otomatis dari tarif kotor.")
            FeatureRow(Icons.Filled.LocalGasStation, "Pantau Bensin & Servis", "Hitung km/liter & alokasi biaya perawatan berkala.")
            FeatureRow(Icons.Filled.Warning, "100% Siap Offline", "Catat trip di jalan tanpa takut sinyal ponsel hilang.", muted = true)

            // Indikator halaman
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.width(34.dp).height(8.dp).clip(CircleShape).background(OjolColors.Primary))
                Spacer(Modifier.width(6.dp))
                Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFBDC9C7)))
                Spacer(Modifier.width(6.dp))
                Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFBDC9C7)))
            }

            PrimaryButton(
                text = "Mulai Sekarang",
                onClick = onStart,
                trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                height = 56.dp,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Sudah punya akun? ", style = OjolText.BodyLg, color = OjolColors.TextPrimary)
                Text(
                    "Masuk",
                    style = OjolText.BodyLg.copy(fontWeight = FontWeight.Bold),
                    color = OjolColors.Primary,
                    modifier = Modifier.clickable(onClick = onLogin),
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = OjolColors.Primary, modifier = Modifier.size(18.dp))
                Text(
                    "100% Gratis & Terenkripsi Aman untuk Mitra Pengemudi Indonesia",
                    style = OjolText.BodyMd,
                    color = OjolColors.TextVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HeroPreviewCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF9CF1E8), Color(0xFFE3FAF6))))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Pill("Tersinkron Otomatis", bg = Color.White, fg = OjolColors.PrimaryDeep, icon = Icons.Filled.Sync)
            Pill("Kebal Blank Spot", bg = Color.White, fg = OjolColors.PrimaryDeep, icon = Icons.Filled.WifiOff)
        }

        OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), padding = 16.dp) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Untung Bersih Hari Ini", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Rp125.000", style = OjolText.Display.copy(fontSize = 34.sp), color = OjolColors.TextPrimary)
                        Spacer(Modifier.width(8.dp))
                        Pill("↑18%", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep)
                    }
                }
                IconBox(Icons.Filled.TwoWheeler, bg = OjolColors.GrayBox, tint = OjolColors.PrimaryDeep, size = 50.dp)
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(OjolColors.GrayBox)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PlatformMini("GoRide 20%", OjolColors.GoRideText, Modifier.weight(1f))
                PlatformMini("Grab 20%", OjolColors.GrabText, Modifier.weight(1f))
                PlatformMini("Maxim 15%", OjolColors.Warning, Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Filled.LocalGasStation, contentDescription = null, tint = OjolColors.TextVariant, modifier = Modifier.size(18.dp))
                    Text("Bensin: Rp35.000 (3.2 L)", style = OjolText.BodyMd, color = OjolColors.TextPrimary)
                }
                Text("14 Trip Selesai", style = OjolText.LabelMd, color = OjolColors.PrimaryDeep)
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Filled.Bolt, contentDescription = null, tint = OjolColors.PrimaryDeep, modifier = Modifier.size(16.dp))
                Text("Offline-First Database Aktif", style = OjolText.LabelMd, color = OjolColors.PrimaryDeep)
            }
            Text("Update Realtime", style = OjolText.LabelMd, color = OjolColors.TextSecondary)
        }
    }
}

@Composable
private fun PlatformMini(text: String, dot: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(dot))
        Text(text, style = OjolText.LabelMd, color = OjolColors.TextPrimary, maxLines = 2)
    }
}

@Composable
private fun BannerCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF3B4A46), Color(0xFF1F2A28), Color(0xFF0E1514)),
                ),
            ),
    ) {
        Icon(
            Icons.Filled.TwoWheeler,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.12f),
            modifier = Modifier.size(150.dp).align(Alignment.CenterEnd).padding(end = 12.dp),
        )
        Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text("DIRANCANG KHUSUS", style = OjolText.LabelSm.copy(fontSize = 11.sp), color = Color(0xFF7CEBDD))
            Text(
                "Fokus Narik, Hitungan Kas Beres Otomatis",
                style = OjolText.TitleLg.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
        }
    }
}

@Composable
private fun FeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    muted: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OjolColors.GrayBox)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconBox(
            icon,
            bg = if (muted) OjolColors.GrayBox2 else Color(0xFF8FEFE4),
            tint = if (muted) OjolColors.PrimaryDeep else OjolColors.PrimaryDeep,
            size = 44.dp,
            radius = 12.dp,
        )
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = OjolText.TitleMd.copy(fontWeight = FontWeight.SemiBold), color = OjolColors.TextPrimary)
            Text(subtitle, style = OjolText.BodyMd, color = OjolColors.TextVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
