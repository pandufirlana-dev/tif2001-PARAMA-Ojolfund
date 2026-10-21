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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojolfund.app.ui.components.IconBox
import com.ojolfund.app.ui.components.OjolCard
import com.ojolfund.app.ui.components.PageHeader
import com.ojolfund.app.ui.components.Pill
import com.ojolfund.app.ui.components.ProgressLine
import com.ojolfund.app.ui.components.toast
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText
import kotlinx.coroutines.delay

private data class ShiftActivity(
    val title: String,
    val tag: String,
    val subtitle: String,
    val amount: String,
    val caption: String,
    val income: Boolean,
    val badge: String
)

private val shiftActivities = listOf(
    ShiftActivity("GoRide #1084", "Trip", "11:42 WIB • Antar St. Tebet", "+Rp18.000", "Bersih", true, "GR"),
    ShiftActivity(
        "Pertalite 2.5L SP...",
        "BBM",
        "10:15 WIB • SPBU MT Haryono",
        "-Rp25.000",
        "Pengeluaran",
        false,
        "BBM"
    ),
    ShiftActivity("GrabBike #2291", "Trip", "09:30 WIB • Mall Kota Kasablanka", "+Rp15.200", "Bersih", true, "GB"),
    ShiftActivity(
        "Parkir Resmi Stas...",
        "Parkir",
        "08:45 WIB • Stasiun Cawang",
        "-Rp3.000",
        "Pengeluaran",
        false,
        "P"
    )
)

@Composable
fun ShiftScreen(onProfile: () -> Unit) {
    val context = LocalContext.current
    var elapsed by remember { mutableIntStateOf(4 * 3600 + 45 * 60 + 15) }
    var paused by remember { mutableStateOf(false) }
    var showFinish by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }

    LaunchedEffect(paused, finished) {
        while (!paused && !finished) {
            delay(1000)
            elapsed += 1
        }
    }

    val h = elapsed / 3600
    val m = (elapsed % 3600) / 60
    val s = elapsed % 60
    val timer = "%02dj : %02dm : %02dd".format(h, m, s)

    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        PageHeader(
            title = "Shift",
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
            // Kartu timer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFFE3FAF6), Color.White)))
                    .border(1.dp, OjolColors.Border, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Pill(
                        if (finished) {
                            "SHIFT SELESAI"
                        } else if (paused) {
                            "SHIFT DIJEDA"
                        } else {
                            "SHIFT AKTIF (ON-BID)"
                        },
                        bg = OjolColors.Aqua,
                        fg = OjolColors.PrimaryDeep,
                        dot = true,
                        textSize = 12.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = OjolColors.TextVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("Hari Ini, 24 Okt", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(OjolColors.GrayBox2)
                        .padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "DURASI NARIK",
                        style = OjolText.LabelLg.copy(letterSpacing = 1.sp),
                        color = OjolColors.TextVariant
                    )
                    Text(
                        timer,
                        style = OjolText.Display.copy(fontSize = 38.sp),
                        color = OjolColors.TextPrimary,
                        maxLines = 1
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = OjolColors.Primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text("Mulai: 07:30 WIB", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                    }
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Filled.Bolt,
                            contentDescription = null,
                            tint = OjolColors.Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("GPS Akurat", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(OjolColors.Primary))
                        Text("Mode Multi-App Siaga", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                    }
                }
            }

            // Untung bersih shift
            OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), padding = 16.dp) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Untung Bersih Shift Ini", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                        Text("Rp110.000", style = OjolText.Display.copy(fontSize = 38.sp), color = OjolColors.Primary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Pill(
                            "Produktif",
                            bg = OjolColors.GrayBox2,
                            fg = OjolColors.TextPrimary,
                            icon = Icons.Filled.TrendingUp,
                            textSize = 12.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        Text("Rp23.150 / Jam Narik", style = OjolText.LabelMd, color = OjolColors.TextVariant)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShiftMini("Kotor (8 Trip)", "Rp145rb", OjolColors.TextPrimary, Modifier.weight(1f))
                    ShiftMini("Pot. Platform", "-Rp20rb", OjolColors.ExpenseText, Modifier.weight(1f))
                    ShiftMini("BBM & Parkir", "-Rp15rb", OjolColors.ExpenseText, Modifier.weight(1f))
                }
            }

            // Target tupo
            OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), padding = 16.dp) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconBox(
                        Icons.Filled.EmojiEvents,
                        bg = OjolColors.Aqua,
                        tint = OjolColors.PrimaryDeep,
                        size = 46.dp,
                        radius = 23.dp
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Target Tupo Harian",
                            style = OjolText.TitleLg.copy(fontWeight = FontWeight.Bold),
                            color = OjolColors.TextPrimary
                        )
                        Text("Bonus insentif tambahan", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                    }
                    Text(
                        "+Rp35.000",
                        style = OjolText.TitleLg.copy(fontWeight = FontWeight.Bold),
                        color = OjolColors.Primary
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("8 dari 12 Order Tercapai", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                    Text("66%", style = OjolText.LabelLg, color = OjolColors.Primary)
                }
                Spacer(Modifier.height(8.dp))
                ProgressLine(progress = 0.66f, color = OjolColors.Primary, height = 10.dp)
                Spacer(Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Flag,
                        contentDescription = null,
                        tint = OjolColors.Primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        buildAnnotatedString {
                            append("Sisa ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("4 order lagi") }
                            append(" untuk cairkan bonus sore!")
                        },
                        style = OjolText.BodyMd,
                        color = OjolColors.TextVariant
                    )
                }
            }

            // Aktivitas shift
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Aktivitas Shift Ini",
                    style = OjolText.TitleLg.copy(fontWeight = FontWeight.Bold),
                    color = OjolColors.TextPrimary
                )
                Text("4 Catatan Masuk", style = OjolText.LabelMd, color = OjolColors.TextVariant)
            }
            OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), padding = 14.dp) {
                shiftActivities.forEachIndexed { index, a ->
                    ShiftActivityRow(a)
                    if (index < shiftActivities.lastIndex) Spacer(Modifier.height(14.dp))
                }
            }

            // Tombol aksi
            PauseButton(
                text = if (paused) "Lanjutkan Shift" else "Jeda Istirahat (Pause)",
                resume = paused,
                enabled = !finished,
                onClick = {
                    paused = !paused
                    context.toast(if (paused) "Shift dijeda" else "Shift dilanjutkan")
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (finished) OjolColors.ShiftRed.copy(alpha = 0.5f) else OjolColors.ShiftRed)
                    .then(if (finished) Modifier else Modifier.clickable { showFinish = true }),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.StopCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Selesaikan Shift & Rekap Cuan",
                    style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    if (showFinish) {
        AlertDialog(
            onDismissRequest = { showFinish = false },
            title = { Text("Selesaikan shift?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Durasi narik $timer. Untung bersih shift ini Rp110.000 dari 8 trip. Data akan direkap ke buku kas."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showFinish = false
                    finished = true
                    context.toast("Shift selesai. Rekap tersimpan.")
                }) { Text("Selesaikan", color = OjolColors.ShiftRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showFinish = false }) { Text("Batal", color = OjolColors.TextVariant) }
            }
        )
    }
}

@Composable
private fun PauseButton(text: String, resume: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(OjolColors.GrayBox2)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (resume) Icons.Filled.PlayArrow else Icons.Filled.FreeBreakfast,
            contentDescription = null,
            tint = OjolColors.TextPrimary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(text, style = OjolText.TitleMd, color = OjolColors.TextPrimary)
    }
}

@Composable
private fun ShiftMini(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(OjolColors.GrayBox)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Text(label, style = OjolText.LabelSm.copy(fontSize = 11.sp), color = OjolColors.TextVariant, maxLines = 1)
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp),
            color = valueColor,
            maxLines = 1
        )
    }
}

@Composable
private fun ShiftActivityRow(a: ShiftActivity) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        val bg = if (a.income) OjolColors.GrayBox2 else OjolColors.ExpenseBg
        val fg = if (a.income) OjolColors.PrimaryDeep else OjolColors.Expense
        Box(Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(bg), contentAlignment = Alignment.Center) {
            Text(
                a.badge,
                color = fg,
                fontWeight = FontWeight.ExtraBold,
                fontSize = if (a.badge.length >
                    1
                ) {
                    13.sp
                } else {
                    22.sp
                }
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    a.title,
                    style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                    color = OjolColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Pill(
                    a.tag,
                    bg = if (a.income) OjolColors.GrayBox2 else OjolColors.ExpenseBg,
                    fg = if (a.income) OjolColors.TextVariant else OjolColors.ExpenseText,
                    textSize = 10.sp
                )
            }
            Text(a.subtitle, style = OjolText.BodySm, color = OjolColors.TextVariant, maxLines = 2)
        }
        Spacer(Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                a.amount,
                style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                color = if (a.income) OjolColors.Primary else OjolColors.ExpenseText,
                maxLines = 1
            )
            Text(a.caption, style = OjolText.LabelSm.copy(fontSize = 11.sp), color = OjolColors.TextVariant)
        }
    }
}
