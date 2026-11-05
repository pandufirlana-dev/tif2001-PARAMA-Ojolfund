package com.ojolfund.app.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojolfund.app.data.DaySummary
import com.ojolfund.app.data.SummaryData
import com.ojolfund.app.data.formatDecimal
import com.ojolfund.app.data.formatRupiah
import com.ojolfund.app.ui.components.DetailHeader
import com.ojolfund.app.ui.components.IconBox
import com.ojolfund.app.ui.components.OjolCard
import com.ojolfund.app.ui.components.Pill
import com.ojolfund.app.ui.components.PrimaryButton
import com.ojolfund.app.ui.components.ProgressLine
import com.ojolfund.app.ui.components.SoftButton
import com.ojolfund.app.ui.components.growX
import com.ojolfund.app.ui.components.rememberGrow
import com.ojolfund.app.ui.components.toast
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText

/** Ringkasan Harian: untung bersih, kontribusi platform, beban operasional, dan shift. */
@Composable
fun SummaryScreen(onBack: () -> Unit, onSeeTransactions: () -> Unit) {
    val context = LocalContext.current
    var dayIndex by rememberSaveable { mutableIntStateOf(0) }
    val days = SummaryData.days
    val day = days[dayIndex]

    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        DetailHeader(
            title = "Ringkasan Harian",
            subtitle = "Laporan Performa & Buku Kas Harian",
            onBack = onBack,
            trailing = {
                IconBox(
                    Icons.Filled.CalendarMonth,
                    size = 40.dp,
                    radius = 20.dp,
                    modifier = Modifier.clickable { context.toast("Pemilih tanggal (demo)") }
                )
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DateSelector(
                label = day.dateLabel,
                isToday = day.isToday,
                canPrev = dayIndex < days.lastIndex,
                canNext = dayIndex > 0,
                onPrev = { dayIndex += 1 },
                onNext = { dayIndex -= 1 }
            )

            NetHeroCard(day)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniStat(
                    icon = Icons.Filled.CheckCircle,
                    label = "Order Selesai",
                    value = "${day.orders} Trip",
                    note = "${day.cancelled} Batal" + if (day.cancelled == 0) " (100% Tupo)" else "",
                    modifier = Modifier.weight(1f)
                )
                MiniStat(
                    icon = Icons.Filled.Payments,
                    label = "Rata-rata Bersih",
                    value = formatRupiah(day.avgNetPerTrip),
                    note = "per trip terselesaikan",
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniStat(
                    icon = Icons.Filled.Schedule,
                    label = "Durasi On-Bid",
                    value = "%02dj : %02dm".format(day.onBidMinutes / 60, day.onBidMinutes % 60),
                    note = "Efektif di jalan",
                    modifier = Modifier.weight(1f)
                )
                MiniStat(
                    icon = Icons.Filled.Speed,
                    label = "Cuan per Jam",
                    value = formatRupiah(day.netPerHour),
                    note = "Produktif ⭐",
                    noteColor = OjolColors.Primary,
                    modifier = Modifier.weight(1f)
                )
            }

            PlatformCard(day)
            OperationalCard(day)
            ShiftCard(day)

            PrimaryButton(
                text = "Unduh Laporan Harian (PDF / Struk)",
                onClick = { context.toast("Laporan PDF disiapkan (demo)") },
                leadingIcon = Icons.Filled.Download,
                modifier = Modifier.testTag("summary_download")
            )
            SoftButton(
                text = "Bagikan Rekap Cuan ke Komunitas",
                onClick = { shareText(context, shareMessage(day)) },
                leadingIcon = Icons.Filled.Share,
                bg = OjolColors.AquaSoft,
                fg = OjolColors.PrimaryDeep,
                modifier = Modifier.fillMaxWidth().testTag("summary_share")
            )
            Row(
                Modifier.fillMaxWidth().clickable(onClick = onSeeTransactions).padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Lihat Semua ${day.orders} Transaksi ${if (day.isToday) "Hari Ini" else "Hari Itu"}",
                    style = OjolText.LabelLg,
                    color = OjolColors.Primary
                )
                Spacer(Modifier.width(6.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = OjolColors.Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun shareMessage(day: DaySummary): String = "Rekap cuan ${day.dateLabel} (OjoLFund)\n" +
    "Untung bersih: ${formatRupiah(day.net)} dari ${day.orders} trip\n" +
    "Kotor ${formatRupiah(day.gross)} • Komisi ${formatRupiah(day.commission)} • " +
    "Operasional ${formatRupiah(day.operational)}\n" +
    "Cuan/jam: ${formatRupiah(day.netPerHour)}"

private fun shareText(context: android.content.Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    try {
        context.startActivity(Intent.createChooser(intent, "Bagikan rekap cuan"))
    } catch (_: ActivityNotFoundException) {
        context.toast("Tidak ada aplikasi untuk berbagi")
    }
}

@Composable
private fun DateSelector(
    label: String,
    isToday: Boolean,
    canPrev: Boolean,
    canNext: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    OjolCard(padding = 8.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ArrowButton(forward = false, enabled = canPrev, onClick = onPrev)
            Row(
                Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = OjolColors.Primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(label, style = OjolText.LabelLg, color = OjolColors.TextPrimary, maxLines = 1)
                if (isToday) {
                    Spacer(Modifier.width(6.dp))
                    Pill("Hari Ini", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep)
                }
            }
            ArrowButton(forward = true, enabled = canNext, onClick = onNext)
        }
    }
}

@Composable
private fun ArrowButton(forward: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (enabled) OjolColors.GrayBox2 else OjolColors.GrayBox)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            if (forward) Icons.AutoMirrored.Filled.KeyboardArrowRight else Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = if (forward) "Hari berikutnya" else "Hari sebelumnya",
            tint = if (enabled) OjolColors.TextPrimary else OjolColors.TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun NetHeroCard(day: DaySummary) {
    OjolCard(padding = 18.dp) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "UNTUNG BERSIH RIIL",
                style = OjolText.LabelMd,
                color = OjolColors.TextVariant,
                modifier = Modifier.weight(1f)
            )
            val delta = day.targetPct - 100
            Pill(
                text = (if (delta >= 0) "+" else "") + "$delta% Target",
                bg = OjolColors.Aqua,
                fg = OjolColors.PrimaryDeep,
                icon = Icons.Filled.TrendingUp
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            formatRupiah(day.net),
            style = OjolText.Display,
            color = OjolColors.Primary,
            modifier = Modifier.testTag("summary_net")
        )
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "Target Harian: ${formatRupiah(day.dailyTarget)}",
                style = OjolText.BodySm,
                color = OjolColors.TextVariant
            )
            Text("Tercapai ${day.targetPct}%", style = OjolText.LabelMd, color = OjolColors.Primary)
        }
        Spacer(Modifier.height(6.dp))
        ProgressLine(progress = day.targetPct / 100f, color = OjolColors.Primary)
        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(OjolColors.GrayBox)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EquationPart("Kotor", compact(day.gross), "${day.orders} Order", OjolColors.TextPrimary)
            Text("–", color = OjolColors.TextSecondary)
            EquationPart(
                "Komisi",
                compact(day.commission),
                formatDecimal(day.commissionPct, 1) + "%",
                OjolColors.ExpenseText
            )
            Text("–", color = OjolColors.TextSecondary)
            EquationPart("Operasional", compact(day.operational), "BBM+Parkir", OjolColors.ExpenseText)
            Text("=", color = OjolColors.TextSecondary)
            EquationPart("Bersih", compact(day.net), "Kantong", OjolColors.Primary)
        }
    }
}

private fun compact(value: Long): String = "Rp" + (value / 1_000) + "k"

@Composable
private fun EquationPart(title: String, value: String, note: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = OjolText.LabelSm.copy(fontSize = 10.sp), color = color)
        Text(value, style = OjolText.LabelLg, color = color)
        Text(note, style = OjolText.BodySm.copy(fontSize = 10.sp), color = OjolColors.TextVariant)
    }
}

@Composable
private fun MiniStat(
    icon: ImageVector,
    label: String,
    value: String,
    note: String,
    modifier: Modifier = Modifier,
    noteColor: Color = OjolColors.TextVariant
) {
    OjolCard(modifier = modifier) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = OjolText.BodySm, color = OjolColors.TextVariant, modifier = Modifier.weight(1f))
            IconBox(icon, size = 32.dp, radius = 16.dp, iconSize = 18.dp)
        }
        Spacer(Modifier.height(6.dp))
        Text(value, style = OjolText.HeadlineMd.copy(fontWeight = FontWeight.Bold), color = OjolColors.TextPrimary)
        Text(note, style = OjolText.LabelSm.copy(fontSize = 11.sp), color = noteColor)
    }
}

@Composable
private fun PlatformCard(day: DaySummary) {
    val colors = listOf(OjolColors.Primary, OjolColors.Emerald, OjolColors.Warning)
    OjolCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Kontribusi Platform Mitra",
                style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            Text("${day.platforms.size} Aplikasi Aktif", style = OjolText.LabelMd, color = OjolColors.TextVariant)
        }
        Spacer(Modifier.height(10.dp))
        val grow = rememberGrow(delayMillis = 200)
        Row(
            Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).growX(grow),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            day.platforms.forEachIndexed { i, p ->
                Box(
                    Modifier
                        .weight(p.sharePct.toFloat().coerceAtLeast(1f))
                        .height(8.dp)
                        .background(colors[i % colors.size])
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            day.platforms.forEach { p ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(OjolColors.GrayBox)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(OjolColors.GrayBox2),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(p.code, style = OjolText.LabelMd, color = OjolColors.TextVariant)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(p.name, style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                        Text(
                            "${p.trips} Trip • ${formatDecimal(p.sharePct, 1)}% share",
                            style = OjolText.BodySm,
                            color = OjolColors.TextVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(formatRupiah(p.net), style = OjolText.TitleMd, color = OjolColors.Primary)
                        Text("Kotor ${formatRupiah(p.gross)}", style = OjolText.BodySm, color = OjolColors.TextVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun OperationalCard(day: DaySummary) {
    OjolCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.LocalGasStation,
                contentDescription = null,
                tint = OjolColors.ExpenseText,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Beban Operasional",
                style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                color = OjolColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(formatRupiah(-day.operational), style = OjolText.TitleMd, color = OjolColors.ExpenseText)
        }
        Spacer(Modifier.height(8.dp))
        ExpenseLine(day.fuelNote, -day.fuelCost)
        ExpenseLine(day.parkingNote, -day.parkingCost)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(OjolColors.GrayBox)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBox(Icons.Filled.TwoWheeler, size = 36.dp, radius = 18.dp, iconSize = 20.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("Jarak Tempuh Shift", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                Text(
                    "Total ${formatDecimal(day.distanceKm, 1)} km narik",
                    style = OjolText.BodySm,
                    color = OjolColors.TextVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${formatDecimal(day.kmPerLiter, 1)} km/L", style = OjolText.TitleMd, color = OjolColors.Primary)
                Text(
                    if (day.kmPerLiter >= EFFICIENT_KM_PER_LITER) "Konsumsi Sangat Irit" else "Konsumsi Normal",
                    style = OjolText.BodySm,
                    color = OjolColors.TextVariant
                )
            }
        }
    }
}

private const val EFFICIENT_KM_PER_LITER = 40.0

@Composable
private fun ExpenseLine(label: String, amount: Long) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = OjolText.BodyMd, color = OjolColors.TextPrimary, modifier = Modifier.weight(1f))
        Text(formatRupiah(amount), style = OjolText.LabelLg, color = OjolColors.TextPrimary)
    }
}

@Composable
private fun ShiftCard(day: DaySummary) {
    OjolCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Schedule,
                contentDescription = null,
                tint = OjolColors.TextPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Shift & Waktu Istirahat",
                style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            Pill(day.shiftName, bg = OjolColors.GrayBox2, fg = OjolColors.TextVariant)
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TimeBox("Jam Mulai", day.shiftStart, "WIB", Modifier.weight(1f))
            TimeBox("Selesai", day.shiftEnd, "WIB", Modifier.weight(1f))
            TimeBox("Istirahat", day.restLabel, day.restPlace, Modifier.weight(1f), OjolColors.Primary)
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(OjolColors.GrayBox)
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Filled.Edit,
                contentDescription = null,
                tint = OjolColors.TextVariant,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Catatan Harian Anda:", style = OjolText.LabelMd, color = OjolColors.TextPrimary)
                Text("\"${day.note}\"", style = OjolText.BodyMd, color = OjolColors.TextVariant)
            }
        }
    }
}

@Composable
private fun TimeBox(
    label: String,
    value: String,
    note: String,
    modifier: Modifier = Modifier,
    valueColor: Color = OjolColors.TextPrimary
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(OjolColors.GrayBox)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = OjolText.BodySm, color = OjolColors.TextVariant)
        Text(value, style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold), color = valueColor)
        Text(note, style = OjolText.BodySm.copy(fontSize = 10.sp), color = OjolColors.TextVariant)
    }
}
