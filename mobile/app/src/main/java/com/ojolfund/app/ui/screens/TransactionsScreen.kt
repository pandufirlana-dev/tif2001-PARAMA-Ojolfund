package com.ojolfund.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojolfund.app.data.SampleData
import com.ojolfund.app.data.Transaction
import com.ojolfund.app.data.formatRupiah
import com.ojolfund.app.ui.components.OjolCard
import com.ojolfund.app.ui.components.OjolTopBar
import com.ojolfund.app.ui.components.Pill
import com.ojolfund.app.ui.components.TagIcon
import com.ojolfund.app.ui.components.tagStyle
import com.ojolfund.app.ui.components.toast
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText
import com.ojolfund.app.viewmodel.LedgerViewModel

@Composable
fun TransactionsScreen(ledgerVm: LedgerViewModel, onAddTransaction: () -> Unit, onProfile: () -> Unit) {
    val context = LocalContext.current
    val all by ledgerVm.transactions.collectAsState()
    var filter by remember { mutableIntStateOf(0) } // 0 semua, 1 pendapatan, 2 pengeluaran

    val shown = when (filter) {
        1 -> all.filter { it.isIncome }
        2 -> all.filter { !it.isIncome }
        else -> all
    }
    val grouped = shown.groupBy { it.group }

    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        OjolTopBar(
            subtitle = "Transaksi",
            onBell = { context.toast("Belum ada notifikasi baru") },
            onAvatar = onProfile
        )

        Box(Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Judul + filter bulan
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Riwayat Transaksi",
                            style = OjolText.HeadlineLg.copy(fontSize = 26.sp),
                            color = OjolColors.TextPrimary
                        )
                        Text("Buku kas harian multi-platform", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(OjolColors.GrayBox2)
                            .clickable { context.toast("Filter bulan (demo): Okt 2024") }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = OjolColors.PrimaryDeep,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("Okt 2024", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                        Icon(
                            Icons.Filled.ExpandMore,
                            contentDescription = null,
                            tint = OjolColors.TextVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Rekap bulan ini
                OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), padding = 14.dp) {
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
                                Icons.Filled.Analytics,
                                contentDescription = null,
                                tint = OjolColors.PrimaryDeep,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                "REKAP BULAN INI",
                                style = OjolText.LabelSm.copy(fontSize = 11.sp),
                                color = OjolColors.TextPrimary
                            )
                        }
                        Pill("1 - 24 Okt", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep, textSize = 11.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RecapBox(
                            "Pemasukan",
                            "+3.250....",
                            "18 trip",
                            OjolColors.Emerald,
                            OjolColors.GrayBox,
                            Modifier.weight(1f)
                        )
                        RecapBox(
                            "Pengeluaran",
                            "-780.000",
                            "6 pos",
                            OjolColors.ExpenseText,
                            OjolColors.GrayBox,
                            Modifier.weight(1f)
                        )
                        RecapBox(
                            "Sisa Untung",
                            "2.470.000",
                            "↗ 76%",
                            OjolColors.PrimaryDeep,
                            Color(0xFFE3EFEE),
                            Modifier.weight(1f)
                        )
                    }
                }

                // Chip filter
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChipItem("Semua (${all.size + 18})", filter == 0) { filter = 0 }
                    FilterChipItem("Pendapatan (18)", filter == 1) { filter = 1 }
                    FilterChipItem("Pengeluaran (6)", filter == 2) { filter = 2 }
                }

                // Daftar per hari
                grouped.forEach { (group, items) ->
                    GroupHeader(group)
                    items.forEach { tx ->
                        TransactionCard(tx, onClick = { context.toast("${tx.tag}: ${tx.title}") })
                    }
                }

                if (shown.isEmpty()) {
                    Text(
                        "Belum ada transaksi pada filter ini.",
                        style = OjolText.BodyMd,
                        color = OjolColors.TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }

                // Info bawah
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(OjolColors.GrayBox)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        Modifier.size(56.dp).clip(CircleShape).background(OjolColors.Aqua),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Receipt,
                            contentDescription = null,
                            tint = OjolColors.PrimaryDeep,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        "Data Transaksi Rapi & Tersinkron",
                        style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                        color = OjolColors.TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Semua pemasukan dari 3 aplikasi mitra otomatis terhubung dengan buku kas harianmu.",
                        style = OjolText.BodyMd,
                        color = OjolColors.TextVariant,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(72.dp))
            }

            // Tombol melayang
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(OjolColors.Primary)
                    .clickable(onClick = onAddTransaction)
                    .padding(horizontal = 36.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    Icons.Filled.AddCircleOutline,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    "Catat Transaksi Baru",
                    style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun RecapBox(
    label: String,
    value: String,
    caption: String,
    valueColor: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = OjolText.LabelSm.copy(fontSize = 11.sp), color = OjolColors.TextPrimary, maxLines = 1)
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp),
            color = valueColor,
            maxLines = 1
        )
        Text(
            caption,
            style = OjolText.LabelSm.copy(fontSize = 11.sp),
            color = if (caption.startsWith("↗")) OjolColors.PrimaryDeep else OjolColors.PrimaryDeep
        )
    }
}

@Composable
private fun FilterChipItem(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text,
        style = OjolText.LabelLg,
        color = if (selected) Color.White else OjolColors.TextPrimary,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) OjolColors.PrimaryDeep else Color.White)
            .border(1.dp, if (selected) OjolColors.PrimaryDeep else OjolColors.Border, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        maxLines = 1
    )
}

@Composable
private fun GroupHeader(group: String) {
    val net = if (group == SampleData.GROUP_TODAY) "Bersih: +Rp125.000" else "Bersih: +Rp142.000"
    val isToday = group == SampleData.GROUP_TODAY
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                Modifier.size(8.dp).clip(CircleShape).background(if (isToday) OjolColors.Primary else Color(0xFFBDC9C7))
            )
            Text(group, style = OjolText.TitleMd.copy(fontWeight = FontWeight.SemiBold), color = OjolColors.TextPrimary)
        }
        Spacer(Modifier.width(8.dp))
        Pill(
            net,
            bg = if (isToday) OjolColors.Aqua else OjolColors.GrayBox2,
            fg = if (isToday) OjolColors.PrimaryDeep else OjolColors.TextVariant,
            textSize = 10.sp
        )
    }
}

@Composable
private fun TransactionCard(tx: Transaction, onClick: () -> Unit) {
    val style = tagStyle(tx.tagKind)
    OjolCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        padding = 14.dp,
        onClick = onClick
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TagIcon(tx.tagKind, size = 48.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Pill(tx.tag, bg = style.bg, fg = style.fg, textSize = 10.sp)
                    Text(tx.time, style = OjolText.BodyMd, color = OjolColors.TextVariant)
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    tx.title,
                    style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                    color = OjolColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    tx.subtitle,
                    style = OjolText.BodyMd.copy(fontSize = 13.sp),
                    color = OjolColors.TextVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatRupiah(tx.amount, withSign = true),
                    style = OjolText.TitleLg.copy(fontWeight = FontWeight.Bold),
                    color = if (tx.isIncome) OjolColors.Emerald else OjolColors.ExpenseText,
                    maxLines = 1
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    if (tx.isIncome) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = OjolColors.Emerald,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Text(
                        tx.method,
                        style = OjolText.LabelSm.copy(fontSize = 11.sp),
                        color = if (tx.isIncome) OjolColors.Emerald else OjolColors.TextVariant
                    )
                }
            }
        }
    }
}
