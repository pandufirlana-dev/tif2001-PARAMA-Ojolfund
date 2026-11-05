package com.ojolfund.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.VerifiedUser
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
import com.ojolfund.app.data.PeriodReport
import com.ojolfund.app.data.ReportBar
import com.ojolfund.app.data.ReportData
import com.ojolfund.app.data.formatNumber
import com.ojolfund.app.data.formatRupiah
import com.ojolfund.app.ui.components.DetailHeader
import com.ojolfund.app.ui.components.IconBox
import com.ojolfund.app.ui.components.OjolCard
import com.ojolfund.app.ui.components.Pill
import com.ojolfund.app.ui.components.PrimaryButton
import com.ojolfund.app.ui.components.SoftButton
import com.ojolfund.app.ui.components.growX
import com.ojolfund.app.ui.components.rememberGrow
import com.ojolfund.app.ui.components.toast
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText

/** Analisis & Laporan: tren pendapatan, jam gacor, efisiensi platform, dan ekspor dokumen. */
@Composable
fun ReportScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val period = ReportData.periods[tab]

    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        DetailHeader(
            title = "Analisis & Laporan",
            subtitle = "Performa Finansial & Dokumen Bankable",
            onBack = onBack,
            trailing = {
                IconBox(
                    Icons.Filled.Info,
                    size = 40.dp,
                    radius = 20.dp,
                    modifier = Modifier.clickable {
                        context.toast("Laporan dihitung dari catatan order dan biaya operasionalmu")
                    }
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
            PeriodTabs(selected = tab, onSelect = { tab = it })

            OjolCard(padding = 10.dp) {
                Row(
                    Modifier.fillMaxWidth().clickable { context.toast("Pemilih rentang tanggal (demo)") },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Periode sebelumnya",
                        tint = OjolColors.TextVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = OjolColors.Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(period.rangeLabel, style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Periode berikutnya",
                        tint = OjolColors.TextVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            ReportHero(period)
            TrendCard(period)
            PeakHoursCard()
            PlatformEfficiencyCard()
            DepreciationCard(period.distanceKm)

            ExportCard(
                onPdf = { context.toast("Rekap PDF Bank-Ready disiapkan (demo)") },
                onExcel = { context.toast("Excel dikirim ke email (demo)") }
            )
        }
    }
}

@Composable
private fun PeriodTabs(selected: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(OjolColors.GrayBox2)
            .padding(4.dp)
    ) {
        ReportData.tabs.forEachIndexed { i, label ->
            val active = i == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (active) OjolColors.Card else Color.Transparent)
                    .clickable { onSelect(i) }
                    .padding(vertical = 10.dp)
                    .testTag("report_tab_$i"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    style = OjolText.LabelLg,
                    color = if (active) OjolColors.Primary else OjolColors.TextVariant
                )
            }
        }
    }
}

@Composable
private fun ReportHero(period: PeriodReport) {
    OjolCard(padding = 18.dp) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "UNTUNG BERSIH RIIL",
                style = OjolText.LabelMd,
                color = OjolColors.TextVariant,
                modifier = Modifier.weight(1f)
            )
            Pill(period.deltaLabel, bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep, icon = Icons.Filled.TrendingUp)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "Rp " + formatNumber(period.net),
            style = OjolText.Display,
            color = OjolColors.Primary,
            modifier = Modifier.testTag("report_net")
        )
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(OjolColors.Primary))
            Spacer(Modifier.width(6.dp))
            Text("Status: ${period.statusLabel}", style = OjolText.BodySm, color = OjolColors.TextVariant)
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(OjolColors.GrayBox)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HeroMini(Icons.Filled.Schedule, "Total On-Bid", period.onBidLabel, period.perHourLabel, Modifier.weight(1f))
            HeroMini(
                Icons.Filled.Speed,
                "Jarak & Konsumsi",
                "${formatNumber(period.distanceKm.toLong())} km",
                period.efficiencyLabel,
                Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HeroMini(icon: ImageVector, label: String, value: String, note: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = OjolColors.Primary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(label, style = OjolText.BodySm, color = OjolColors.TextVariant)
        }
        Text(value, style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold), color = OjolColors.TextPrimary)
        Text(note, style = OjolText.BodySm.copy(fontSize = 11.sp), color = OjolColors.TextVariant)
    }
}

@Composable
private fun TrendCard(period: PeriodReport) {
    OjolCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Tren Pendapatan",
                style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            Legend("Kotor", OjolColors.AquaSoft)
            Spacer(Modifier.width(10.dp))
            Legend("Bersih", OjolColors.Primary)
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(OjolColors.GrayBox)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = OjolColors.Primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                period.recordLabel,
                style = OjolText.BodySm,
                color = OjolColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Pill(period.recordTag, bg = OjolColors.Primary, fg = Color.White)
        }
        Spacer(Modifier.height(14.dp))
        BarChart(period.bars)
    }
}

@Composable
private fun Legend(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(4.dp))
        Text(label, style = OjolText.BodySm, color = OjolColors.TextVariant)
    }
}

/** Grafik batang sederhana: batang muda = kotor, batang tua = bersih (tertinggi diberi warna penuh). */
@Composable
private fun BarChart(bars: List<ReportBar>) {
    val maxGross = (bars.maxOfOrNull { it.gross } ?: 1L).coerceAtLeast(1L)
    val best = bars.maxByOrNull { it.net }
    Row(
        modifier = Modifier.fillMaxWidth().height(CHART_HEIGHT.dp + 40.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        bars.forEachIndexed { index, bar ->
            val grow = rememberGrow(delayMillis = index * BAR_STAGGER_MS)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    compactK(bar.gross),
                    style = OjolText.BodySm.copy(fontSize = 10.sp),
                    color = OjolColors.TextVariant
                )
                Spacer(Modifier.height(2.dp))
                val totalHeight = (CHART_HEIGHT * bar.gross / maxGross).toInt().coerceAtLeast(MIN_BAR)
                val netHeight = (CHART_HEIGHT * bar.net / maxGross).toInt().coerceAtLeast(MIN_BAR / 2)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((totalHeight * grow.value).dp)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(OjolColors.AquaSoft),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height((netHeight * grow.value).dp)
                            .background(if (bar == best) OjolColors.Primary else OjolColors.Emerald)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(bar.label, style = OjolText.BodySm, color = OjolColors.TextVariant, maxLines = 1)
            }
        }
    }
}

private const val CHART_HEIGHT = 150
private const val BAR_STAGGER_MS = 80
private const val MIN_BAR = 12

private fun compactK(value: Long): String = (value / 1_000).toString() + "k"

private data class PeakHour(
    val icon: ImageVector,
    val time: String,
    val place: String,
    val rate: Long,
    val tag: String,
    val highlight: Boolean
)

@Composable
private fun PeakHoursCard() {
    val rows = listOf(
        PeakHour(
            Icons.Filled.Schedule,
            "Pagi (06:00 - 09:00)",
            "Rute Stasiun & Sentra Kantor",
            32_000,
            "Stabil",
            false
        ),
        PeakHour(
            Icons.Filled.Restaurant,
            "Siang (11:30 - 13:30)",
            "Gacor Food & Minuman Dingin",
            41_500,
            "Tertinggi",
            true
        ),
        PeakHour(
            Icons.Filled.TwoWheeler,
            "Sore Sibuk (17:00 - 20:00)",
            "Jam Pulang Kerja & Hujan",
            38_000,
            "Tarif Surge",
            false
        )
    )
    OjolCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.TrendingUp,
                contentDescription = null,
                tint = OjolColors.Primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Peta Jam Paling Gacor",
                style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            Text("Efisiensi/Jam", style = OjolText.LabelMd, color = OjolColors.TextVariant)
        }
        Spacer(Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            rows.forEach { r ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (r.highlight) OjolColors.AquaSoft else OjolColors.GrayBox)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconBox(r.icon, size = 36.dp, radius = 18.dp, iconSize = 20.dp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(r.time, style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                            if (r.highlight) {
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = OjolColors.Warning,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(r.place, style = OjolText.BodySm, color = OjolColors.TextVariant)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Rp " + formatNumber(r.rate), style = OjolText.TitleMd, color = OjolColors.TextPrimary)
                        Text("/jam", style = OjolText.BodySm, color = OjolColors.TextVariant)
                        Text(r.tag, style = OjolText.LabelMd, color = OjolColors.Primary)
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(OjolColors.GrayBox2)
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Filled.Lightbulb,
                contentDescription = null,
                tint = OjolColors.PrimaryDeep,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Saran Rute AI: Waktu paling optimal Anda minggu ini adalah Jumat & Sabtu sore di koridor " +
                    "perkantoran Jaksel. Efisiensi bahan bakar naik 14% jika fokus pesanan makanan di siang hari.",
                style = OjolText.BodySm,
                color = OjolColors.TextPrimary
            )
        }
    }
}

@Composable
private fun PlatformEfficiencyCard() {
    OjolCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Efisiensi Platform Mitra",
                style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.PieChart,
                contentDescription = null,
                tint = OjolColors.TextVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        val grow = rememberGrow(delayMillis = 200)
        Row(
            Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).growX(grow),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(Modifier.weight(54f).fillMaxHeight().background(OjolColors.Primary))
            Box(Modifier.weight(34f).fillMaxHeight().background(OjolColors.Emerald))
            Box(Modifier.weight(12f).fillMaxHeight().background(OjolColors.Warning))
        }
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PlatformRow("GoRide / Food", "54% Porsi Order", 980_000, "Potongan Komisi: 15%", "4.95 Rating")
            PlatformRow(
                "GrabBike / Food",
                "34% Porsi Order",
                580_000,
                "Potongan Komisi: 20%",
                "Order Reguler Konsisten"
            )
            PlatformRow("Maxim", "12% Porsi Order", 190_000, "Komisi: 12% (Terendah)", "Margin Bersih 88%")
        }
    }
}

@Composable
private fun PlatformRow(name: String, share: String, net: Long, commission: String, note: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(OjolColors.GrayBox)
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(name, style = OjolText.LabelLg, color = OjolColors.Primary)
            Spacer(Modifier.width(6.dp))
            Text(share, style = OjolText.BodySm, color = OjolColors.TextVariant, modifier = Modifier.weight(1f))
            Text("Rp " + formatNumber(net), style = OjolText.TitleMd, color = OjolColors.TextPrimary)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(commission, style = OjolText.BodySm, color = OjolColors.TextVariant)
            Text(note, style = OjolText.LabelMd, color = OjolColors.TextPrimary)
        }
    }
}

@Composable
private fun DepreciationCard(distanceKm: Int) {
    val allocation = distanceKm * ReportData.DEPRECIATION_PER_KM
    OjolCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBox(Icons.Filled.Build, size = 40.dp, radius = 20.dp, iconSize = 20.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "Biaya Penyusutan Motor",
                    style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                    color = OjolColors.TextPrimary
                )
                Text("Proteksi Aset & Dana Servis Riil", style = OjolText.BodySm, color = OjolColors.TextVariant)
            }
            Text(
                "Odo: ${formatNumber(distanceKm.toLong())} km",
                style = OjolText.BodySm,
                color = OjolColors.TextVariant
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(OjolColors.GrayBox)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Tarif Depresiasi", style = OjolText.BodySm, color = OjolColors.TextVariant)
                Text(
                    "Rp ${ReportData.DEPRECIATION_PER_KM} / km",
                    style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Alokasi Wajib", style = OjolText.BodySm, color = OjolColors.TextVariant)
                Text(
                    formatRupiah(allocation).replace("Rp", "Rp "),
                    style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                    color = OjolColors.Primary
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = OjolColors.Primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "Dana ini diamankan otomatis di kantong Perawatan Motor untuk antisipasi oli, kampas rem, & ban.",
                style = OjolText.BodySm,
                color = OjolColors.TextVariant
            )
        }
    }
}

@Composable
private fun ExportCard(onPdf: () -> Unit, onExcel: () -> Unit) {
    OjolCard(padding = 16.dp) {
        Pill(
            "Standar Bank BRI / Mandiri",
            bg = OjolColors.AquaSoft,
            fg = OjolColors.PrimaryDeep,
            icon = Icons.Filled.AccountBalanceWallet
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Ekspor Rekap Keuangan Resmi",
            style = OjolText.TitleLg.copy(fontWeight = FontWeight.Bold),
            color = OjolColors.TextPrimary
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                Icons.Filled.VerifiedUser,
                contentDescription = null,
                tint = OjolColors.Primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "Laporan penghasilan terverifikasi dengan QR Audit Trail resmi. Format diterima leasing motor baru, " +
                    "KPR bersubsidi, dan pengajuan KUR Mikro hingga Rp 50 Juta.",
                style = OjolText.BodySm,
                color = OjolColors.TextVariant
            )
        }
        Spacer(Modifier.height(12.dp))
        PrimaryButton(
            text = "Unduh Rekap PDF Resmi (Bank-Ready)",
            onClick = onPdf,
            leadingIcon = Icons.Filled.Download,
            modifier = Modifier.testTag("report_pdf")
        )
        Spacer(Modifier.height(8.dp))
        SoftButton(
            text = "Kirim Excel (.xlsx) ke Email",
            onClick = onExcel,
            leadingIcon = Icons.Filled.Email,
            modifier = Modifier.fillMaxWidth().testTag("report_excel")
        )
    }
}
