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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojolfund.app.data.formatRupiah
import com.ojolfund.app.ui.components.IconBox
import com.ojolfund.app.ui.components.OjolCard
import com.ojolfund.app.ui.components.PageHeader
import com.ojolfund.app.ui.components.Pill
import com.ojolfund.app.ui.components.ProgressLine
import com.ojolfund.app.ui.components.toast
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText

@Composable
fun VehicleScreen(onAddFuel: () -> Unit, onProfile: () -> Unit) {
    val context = LocalContext.current
    var odometer by remember { mutableIntStateOf(24_850) }

    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        PageHeader(
            title = "Kendaraan",
            onBell = { context.toast("Belum ada notifikasi baru") },
            onAvatar = onProfile
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Kartu motor
            OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), padding = 16.dp) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    IconBox(
                        Icons.Filled.TwoWheeler,
                        bg = OjolColors.Aqua,
                        tint = OjolColors.PrimaryDeep,
                        size = 56.dp,
                        radius = 14.dp,
                        iconSize = 32.dp
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Honda Vario 160 CBS",
                            style = OjolText.TitleLg.copy(fontWeight = FontWeight.Bold),
                            color = OjolColors.TextPrimary
                        )
                        Pill("2022", bg = OjolColors.GrayBox2, fg = OjolColors.TextVariant, textSize = 11.sp)
                        Text("B 4821 TGB", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(OjolColors.GrayBox2)
                            .clickable { context.toast("Edit data kendaraan (demo)") }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = null,
                            tint = OjolColors.PrimaryDeep,
                            modifier = Modifier.size(16.dp)
                        )
                        Text("Edit", style = OjolText.LabelLg, color = OjolColors.PrimaryDeep)
                    }
                }
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(OjolColors.GrayBox)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Speed,
                        contentDescription = null,
                        tint = OjolColors.PrimaryDeep,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "ODOMETER SEKARANG",
                            style = OjolText.LabelSm.copy(fontSize = 11.sp),
                            color = OjolColors.TextPrimary
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                formatRupiah(odometer.toLong()).removePrefix("Rp"),
                                style = OjolText.HeadlineMd.copy(fontWeight = FontWeight.Bold),
                                color = OjolColors.TextPrimary
                            )
                            Text(" km", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                        }
                    }
                    Pill(
                        "Update hari ini",
                        bg = Color.White,
                        fg = OjolColors.PrimaryDeep,
                        dot = true,
                        modifier = Modifier.clickable {
                            odometer += 12
                            context.toast("Odometer diperbarui (+12 km)")
                        }
                    )
                }
            }

            // Efisiensi & biaya BBM
            OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), padding = 16.dp) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.HealthAndSafety,
                            contentDescription = null,
                            tint = OjolColors.Primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            "Efisiensi & Biaya BBM",
                            style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                            color = OjolColors.TextPrimary
                        )
                    }
                    Pill(
                        "Sangat Irit",
                        bg = OjolColors.Aqua,
                        fg = OjolColors.PrimaryDeep,
                        icon = Icons.Filled.Verified,
                        textSize = 11.sp
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    EffBox(
                        label = "Rata-rata Konsumsi",
                        modifier = Modifier.weight(1f),
                        value = {
                            Text("44,2", style = OjolText.Display.copy(fontSize = 30.sp), color = OjolColors.Primary)
                        },
                        unit = "km / L",
                        caption = "Standar Normal Ojol",
                        withTrend = true
                    )
                    EffBox(
                        label = "Beban Operasional",
                        modifier = Modifier.weight(1f),
                        value = {
                            Text(
                                "Rp226",
                                style = OjolText.Display.copy(fontSize = 30.sp),
                                color = OjolColors.TextPrimary
                            )
                        },
                        unit = "/ km",
                        caption = "Dasar hitung profit bersih",
                        withTrend = false
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(OjolColors.GrayBox)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconBox(
                        Icons.Filled.LocalGasStation,
                        bg = Color.White,
                        tint = OjolColors.PrimaryDeep,
                        size = 40.dp,
                        radius = 20.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Total BBM Oktober",
                            style = OjolText.LabelSm.copy(fontSize = 11.sp),
                            color = OjolColors.TextPrimary
                        )
                        Text(
                            "42 Liter • Pertalite",
                            style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                            color = OjolColors.TextPrimary
                        )
                    }
                    Text(
                        "Rp420.000",
                        style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                        color = OjolColors.TextPrimary
                    )
                }
            }

            // CTA catat BBM
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(OjolColors.Primary)
                    .clickable(onClick = onAddFuel)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.LocalGasStation,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Catat Pengisian BBM Baru",
                        style = OjolText.TitleLg.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        "Hitung konsumsi km/L otomatis",
                        style = OjolText.BodyMd,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Box(
                    Modifier.size(38.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Riwayat isi bensin
            OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), padding = 16.dp) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.History,
                            contentDescription = null,
                            tint = OjolColors.Primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            "Riwayat Isi Bensin",
                            style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                            color = OjolColors.TextPrimary
                        )
                    }
                    Text(
                        "Lihat Semua",
                        style = OjolText.LabelMd,
                        color = OjolColors.Primary,
                        modifier = Modifier.clickable { context.toast("Riwayat lengkap (demo)") }
                    )
                }
                Spacer(Modifier.height(12.dp))
                FuelHistoryCard(
                    "Pertalite 2,5 Liter",
                    "SPBU Margonda • 24 Okt 2024",
                    "Rp25.000",
                    "24.850 km",
                    "43,8 km / Liter"
                )
                Spacer(Modifier.height(12.dp))
                FuelHistoryCard(
                    "Pertalite 3,0 Liter",
                    "SPBU TB Simatupang • 21 Okt 2024",
                    "Rp30.000",
                    "24.740 km",
                    "44,5 km / Liter"
                )
            }

            // Kesehatan motor
            OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), padding = 16.dp) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.Build,
                            contentDescription = null,
                            tint = OjolColors.Primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            "Kesehatan Motor & Servis",
                            style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                            color = OjolColors.TextPrimary
                        )
                    }
                    Text("Berkala Ojol", style = OjolText.LabelMd, color = OjolColors.TextPrimary)
                }
                Spacer(Modifier.height(12.dp))

                ServiceCard(
                    icon = Icons.Filled.Opacity,
                    iconBg = Color(0xFFFFDAD6),
                    iconTint = OjolColors.ShiftRed,
                    title = "Oli Mesin (MPX2)",
                    subtitle = "Terakhir di 23.000 km",
                    badgeText = "Segera Ganti",
                    badgeBg = OjolColors.ShiftRed,
                    badgeFg = Color.White,
                    badgeIcon = Icons.Filled.Warning,
                    progress = 0.9f,
                    progressColor = OjolColors.ShiftRed,
                    left = "Sisa 150 km lagi (Kritis)",
                    right = "Batas: 25.000 km",
                    cardBg = Color(0xFFFFEFED),
                    leftColor = OjolColors.ShiftRed
                )
                Spacer(Modifier.height(10.dp))
                ServiceCard(
                    icon = Icons.Filled.Settings,
                    iconBg = OjolColors.Aqua,
                    iconTint = OjolColors.PrimaryDeep,
                    title = "Servis CVT & Roller",
                    subtitle = "Pembersihan berkala",
                    badgeText = "Aman Terjadwal",
                    badgeBg = OjolColors.Aqua,
                    badgeFg = OjolColors.PrimaryDeep,
                    badgeIcon = null,
                    progress = 0.45f,
                    progressColor = OjolColors.Primary,
                    left = "Sisa 1.150 km lagi",
                    right = "Jadwal: 26.000 km",
                    cardBg = OjolColors.GrayBox,
                    leftColor = OjolColors.TextVariant
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(OjolColors.GrayBox)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconBox(
                        Icons.Filled.Album,
                        bg = OjolColors.GrayBox2,
                        tint = OjolColors.TextVariant,
                        size = 46.dp,
                        radius = 12.dp
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Kampas Rem Depan & Belakang",
                            style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                            color = OjolColors.TextPrimary
                        )
                        Text("Dicek 12 Okt 2024", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                    }
                    Pill("Normal", bg = OjolColors.GrayBox2, fg = OjolColors.TextVariant, textSize = 11.sp)
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(OjolColors.GrayBox2)
                        .clickable { context.toast("Pengingat servis ditambahkan (demo)") },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.AddAlert,
                        contentDescription = null,
                        tint = OjolColors.PrimaryDeep,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Tambah Pengingat Servis", style = OjolText.LabelLg, color = OjolColors.PrimaryDeep)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun EffBox(
    label: String,
    modifier: Modifier = Modifier,
    value: @Composable () -> Unit,
    unit: String,
    caption: String,
    withTrend: Boolean
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(OjolColors.GrayBox)
            .padding(12.dp)
    ) {
        Text(label, style = OjolText.BodyMd, color = OjolColors.TextVariant, maxLines = 1)
        Row(verticalAlignment = Alignment.Bottom) {
            value()
            Spacer(Modifier.width(4.dp))
            Text(
                unit,
                style = OjolText.BodyMd,
                color = OjolColors.TextVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (withTrend) {
                Icon(
                    Icons.Filled.TrendingUp,
                    contentDescription = null,
                    tint = OjolColors.PrimaryDeep,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                caption,
                style = OjolText.LabelSm.copy(fontSize = 11.sp),
                color = if (withTrend) OjolColors.PrimaryDeep else OjolColors.TextVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FuelHistoryCard(title: String, subtitle: String, price: String, odo: String, efficiency: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(top = 2.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            IconBox(
                Icons.Filled.LocalGasStation,
                bg = OjolColors.Aqua,
                tint = OjolColors.PrimaryDeep,
                size = 46.dp,
                radius = 12.dp
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold), color = OjolColors.TextPrimary)
                Text(subtitle, style = OjolText.BodyMd, color = OjolColors.TextVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(price, style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold), color = OjolColors.TextPrimary)
                Text(odo, style = OjolText.LabelSm.copy(fontSize = 11.sp), color = OjolColors.TextVariant)
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(OjolColors.GrayBox)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Efisiensi Trip Terakhir",
                style = OjolText.LabelSm.copy(fontSize = 11.sp),
                color = OjolColors.TextPrimary
            )
            Text(efficiency, style = OjolText.LabelLg, color = OjolColors.Primary)
        }
    }
}

@Composable
private fun ServiceCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    badgeText: String,
    badgeBg: Color,
    badgeFg: Color,
    badgeIcon: ImageVector?,
    progress: Float,
    progressColor: Color,
    left: String,
    right: String,
    cardBg: Color,
    leftColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            IconBox(icon, bg = iconBg, tint = iconTint, size = 46.dp, radius = 12.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold), color = OjolColors.TextPrimary)
                Text(subtitle, style = OjolText.BodyMd, color = OjolColors.TextVariant)
            }
            Pill(badgeText, bg = badgeBg, fg = badgeFg, icon = badgeIcon, textSize = 11.sp)
        }
        Spacer(Modifier.height(10.dp))
        ProgressLine(progress = progress, color = progressColor, track = OjolColors.GrayBox2, height = 8.dp)
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(left, style = OjolText.LabelSm.copy(fontSize = 11.sp), color = leftColor)
            Text(right, style = OjolText.LabelSm.copy(fontSize = 11.sp), color = OjolColors.TextPrimary)
        }
    }
}
