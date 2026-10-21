package com.ojolfund.app.ui.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojolfund.app.ui.components.IconBox
import com.ojolfund.app.ui.components.OjolCard
import com.ojolfund.app.ui.components.OjolTopBar
import com.ojolfund.app.ui.components.Pill
import com.ojolfund.app.ui.components.PrimaryButton
import com.ojolfund.app.ui.components.toast
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    var saveData by remember { mutableStateOf(true) }
    var syncing by remember { mutableStateOf(false) }
    var showLogout by remember { mutableStateOf(false) }
    var gojek by remember { mutableIntStateOf(20) }
    var grab by remember { mutableIntStateOf(20) }
    var maxim by remember { mutableIntStateOf(15) }

    LaunchedEffect(syncing) {
        if (syncing) {
            delay(1500)
            syncing = false
            context.toast("Sinkronisasi selesai. 1.428 catatan aman di cloud")
        }
    }

    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        OjolTopBar(
            subtitle = "Profil — Ojolfund",
            onBell = { context.toast("Belum ada notifikasi baru") },
            onAvatar = { context.toast("Ini profil kamu") }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Kartu profil
            OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), padding = 16.dp) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        Box(
                            Modifier.size(68.dp).clip(RoundedCornerShape(14.dp)).background(OjolColors.Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .clip(CircleShape)
                                .background(Color(0xFFFFC83D))
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFF5C4300),
                                modifier = Modifier.size(11.dp)
                            )
                            Text("4.98", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5C4300))
                        }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Budi Santoso",
                                style = OjolText.TitleLg.copy(fontWeight = FontWeight.Bold),
                                color = OjolColors.TextPrimary
                            )
                            Pill(
                                "Driver Aktif",
                                bg = OjolColors.Aqua,
                                fg = OjolColors.PrimaryDeep,
                                dot = true,
                                textSize = 10.sp
                            )
                        }
                        Text("+62 812-8765-4321", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                        Row(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(OjolColors.GrayBox2)
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Filled.Badge,
                                contentDescription = null,
                                tint = OjolColors.TextVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                "ID: #OJ-88219",
                                style = OjolText.LabelSm.copy(fontSize = 11.sp),
                                color = OjolColors.TextVariant
                            )
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(OjolColors.GrayBox)
                        .padding(12.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            "INTEGRASI AKUN OJOL",
                            style = OjolText.LabelSm.copy(fontSize = 11.sp),
                            color = OjolColors.TextPrimary
                        )
                        Text(
                            "3 Platform Terhubung",
                            style = OjolText.LabelSm.copy(fontSize = 11.sp),
                            color = OjolColors.Primary
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PlatformChip("GoRide", OjolColors.GoRideText, Modifier.weight(1f))
                        PlatformChip("Grab...", OjolColors.GrabText, Modifier.weight(1f))
                        PlatformChip("Maxim", OjolColors.Warning, Modifier.weight(1f))
                    }
                }
            }

            // Pusat sinkronisasi offline
            OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), padding = 16.dp) {
                SectionTitleRow(
                    icon = Icons.Filled.Sync,
                    title = "Pusat Sinkronisasi Offline",
                    subtitle = "Sistem Ledger Kebal Blank Spot",
                    badge = {
                        Pill("Online", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep, dot = true, textSize = 11.sp)
                    }
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(OjolColors.AquaSoft)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Filled.Bolt,
                        contentDescription = null,
                        tint = OjolColors.PrimaryDeep,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "Tetap aman catat trip & beli bensin walau sinyal hilang di terowongan/hujan badai. Transaksi disimpan lokal di HP dan diunggah otomatis saat sinyal pulih.",
                        style = OjolText.BodyMd.copy(fontSize = 13.sp),
                        color = OjolColors.PrimaryDeep
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SyncInfoBox(
                        title = "Antrean Offline",
                        value = if (syncing) "1 item sinkron…" else "0 item pending",
                        footIcon = Icons.Filled.CheckCircle,
                        foot = "Bersih & Terkirim",
                        footColor = OjolColors.Primary,
                        modifier = Modifier.weight(1f)
                    )
                    SyncInfoBox(
                        title = "Aman di Cloud",
                        value = "1.428 catatan",
                        footIcon = Icons.Filled.Schedule,
                        foot = "Terakhir 12:45 WIB",
                        footColor = OjolColors.TextVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))
                PrimaryButton(
                    text = if (syncing) "Menyinkronkan…" else "Sinkronkan Sekarang",
                    onClick = { if (!syncing) syncing = true },
                    leadingIcon = Icons.Filled.Sync,
                    enabled = !syncing
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
                    Icon(
                        Icons.Filled.Wifi,
                        contentDescription = null,
                        tint = OjolColors.PrimaryDeep,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Mode Hemat Kuota Seluler", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                        Text(
                            "Hanya sinkron foto struk saat terhu...",
                            style = OjolText.BodySm,
                            color = OjolColors.TextVariant,
                            maxLines = 1
                        )
                    }
                    Switch(
                        checked = saveData,
                        onCheckedChange = { saveData = it },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = OjolColors.Primary,
                            checkedThumbColor = Color.White,
                            uncheckedTrackColor = OjolColors.GrayBox2,
                            uncheckedThumbColor = Color.White,
                            uncheckedBorderColor = OjolColors.Border
                        )
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(OjolColors.GrayBox2)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Filled.Lightbulb,
                        contentDescription = null,
                        tint = OjolColors.Warning,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "Simulasi otomatis: Saat sinyal terputus, banner kuning \"Mode Offline\" akan muncul tanpa mengganggu tarikan trip.",
                        style = OjolText.BodySm,
                        color = OjolColors.TextVariant
                    )
                }
            }

            // Pencadangan & keamanan
            OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), padding = 16.dp) {
                SectionTitleRow(
                    icon = Icons.Filled.Shield,
                    title = "Pencadangan & Keamanan",
                    subtitle = "Cadangan Cloud Pribadi Driver",
                    badge = { Pill("Tersambung", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep, textSize = 11.sp) }
                )
                Spacer(Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(OjolColors.GrayBox)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoRow(
                        Icons.Filled.Cloud,
                        "Penyimpanan Tujuan",
                        "Google Drive\n(budi.ojol@gmail.com)",
                        OjolColors.TextPrimary
                    )
                    InfoRow(Icons.Filled.Schedule, "Jadwal Otomatis", "Tiap Hari (23:59 WIB)", OjolColors.TextPrimary)
                    InfoRow(Icons.Filled.Fingerprint, "Kunci Biometrik", "Sidik Jari Aktif", OjolColors.Primary)
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlineAction("Cadangkan Manual", Icons.Filled.CloudUpload, Modifier.weight(1f)) {
                        context.toast("Cadangan manual dimulai (demo)")
                    }
                    OutlineAction("Pulihkan Data", Icons.Filled.Restore, Modifier.weight(1f)) {
                        context.toast("Pulihkan data dari Google Drive (demo)")
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(OjolColors.GrayBox2)
                        .clickable { context.toast("Rekap buku kas diunduh (demo)") },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Download,
                        contentDescription = null,
                        tint = OjolColors.TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Unduh Rekap Buku Kas (Excel / PDF)", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                }
            }

            // Pengaturan komisi & target
            OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), padding = 16.dp) {
                SectionTitleRow(
                    icon = Icons.Filled.Tune,
                    title = "Pengaturan Komisi & Target",
                    subtitle = "Hitungan Bersih Otomatis",
                    badge = null
                )
                Spacer(Modifier.height(8.dp))
                CommissionRow("Potongan Gojek (GoRide)", "Komisi standar aplikator", gojek, OjolColors.GoRideText) {
                    gojek =
                        nextPercent(gojek)
                }
                CommissionRow("Potongan Grab (GrabBike)", "Bagi hasil bagi mitra", grab, OjolColors.GrabText) {
                    grab =
                        nextPercent(grab)
                }
                CommissionRow("Potongan Maxim Driver", "Komisi order aktif", maxim, OjolColors.Warning) {
                    maxim =
                        nextPercent(maxim)
                }
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(OjolColors.GrayBox)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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
                                Icons.Filled.Flag,
                                contentDescription = null,
                                tint = OjolColors.Warning,
                                modifier = Modifier.size(20.dp)
                            )
                            Text("Target Cuan Bersih", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                        }
                        Text("Rp 150.000 / hari", style = OjolText.LabelLg, color = OjolColors.Primary)
                    }
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
                                Icons.Filled.Notifications,
                                contentDescription = null,
                                tint = OjolColors.PrimaryDeep,
                                modifier = Modifier.size(20.dp)
                            )
                            Text("Pengingat Servis & Oli", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                        }
                        Pill("Tiap 2.000 KM", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep, textSize = 11.sp)
                    }
                }
            }

            // Bantuan
            OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), padding = 14.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { context.toast("Panduan Driver & FAQ (demo)") }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.MenuBook,
                        contentDescription = null,
                        tint = OjolColors.PrimaryDeep,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Panduan Driver & FAQ", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                        Text(
                            "Tips maksimalkan buku kas saat on-bid",
                            style = OjolText.BodySm,
                            color = OjolColors.TextVariant
                        )
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = OjolColors.TextVariant
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE3F6EC))
                        .clickable { context.toast("Membuka WhatsApp dukungan (demo)") }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.SupportAgent,
                        contentDescription = null,
                        tint = OjolColors.PrimaryDeep,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Dukungan WhatsApp OjoLFund", style = OjolText.LabelLg, color = OjolColors.PrimaryDeep)
                        Text(
                            "Respon cepat tim lapangan (08:00 - 22:00)",
                            style = OjolText.BodySm,
                            color = OjolColors.PrimaryDeep
                        )
                    }
                    Icon(
                        Icons.Filled.Chat,
                        contentDescription = null,
                        tint = OjolColors.PrimaryDeep,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "OjoLFund v1.2.0 (Build 2024.10)",
                    style = OjolText.LabelSm.copy(fontSize = 11.sp),
                    color = OjolColors.TextPrimary
                )
                Text(
                    "100% Khusus Pejuang Aspal & Pengemudi Tangguh",
                    style = OjolText.BodySm,
                    color = OjolColors.TextVariant,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(OjolColors.GrayBox2)
                    .clickable { showLogout = true },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = OjolColors.ShiftRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text("Keluar dari Akun Driver", style = OjolText.LabelLg, color = OjolColors.ShiftRed)
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    if (showLogout) {
        AlertDialog(
            onDismissRequest = { showLogout = false },
            title = { Text("Keluar dari akun?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Data yang belum tersinkron tetap tersimpan di HP dan akan diunggah setelah kamu masuk lagi.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showLogout = false
                    onLogout()
                }) { Text("Keluar", color = OjolColors.ShiftRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showLogout = false }) { Text("Batal", color = OjolColors.TextVariant) }
            }
        )
    }
}

/** Siklus persentase komisi 10..30 (demo). */
private fun nextPercent(current: Int): Int = if (current >= 30) 10 else current + 1

@Composable
private fun PlatformChip(name: String, dot: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(dot))
        Text(
            name,
            style = OjolText.LabelMd,
            color = OjolColors.TextPrimary,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = OjolColors.Primary,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun SectionTitleRow(icon: ImageVector, title: String, subtitle: String, badge: (@Composable () -> Unit)?) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconBox(icon, bg = OjolColors.Aqua, tint = OjolColors.PrimaryDeep, size = 42.dp, radius = 12.dp)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold), color = OjolColors.TextPrimary)
            Text(subtitle, style = OjolText.BodySm, color = OjolColors.TextVariant)
        }
        if (badge != null) badge()
    }
}

@Composable
private fun SyncInfoBox(
    title: String,
    value: String,
    footIcon: ImageVector,
    foot: String,
    footColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(OjolColors.GrayBox)
            .padding(12.dp)
    ) {
        Text(title, style = OjolText.LabelSm.copy(fontSize = 11.sp), color = OjolColors.TextVariant)
        Text(
            value,
            style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
            color = OjolColors.TextPrimary,
            maxLines = 1
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(footIcon, contentDescription = null, tint = footColor, modifier = Modifier.size(13.dp))
            Text(foot, style = OjolText.LabelSm.copy(fontSize = 11.sp), color = footColor, maxLines = 1)
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String, valueColor: Color) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = OjolColors.TextVariant, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, style = OjolText.BodyMd, color = OjolColors.TextVariant, modifier = Modifier.weight(1f))
        Text(value, style = OjolText.LabelLg, color = valueColor, textAlign = TextAlign.End)
    }
}

@Composable
private fun OutlineAction(text: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, OjolColors.Border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = OjolColors.PrimaryDeep, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, style = OjolText.LabelMd, color = OjolColors.PrimaryDeep, maxLines = 1)
    }
}

@Composable
private fun CommissionRow(title: String, subtitle: String, percent: Int, dot: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(dot))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = OjolText.LabelLg, color = OjolColors.TextPrimary)
            Text(subtitle, style = OjolText.BodySm, color = OjolColors.TextVariant)
        }
        Text(
            "$percent%",
            style = OjolText.LabelLg.copy(fontWeight = FontWeight.Bold),
            color = OjolColors.TextPrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(1.dp, OjolColors.Border, RoundedCornerShape(10.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
