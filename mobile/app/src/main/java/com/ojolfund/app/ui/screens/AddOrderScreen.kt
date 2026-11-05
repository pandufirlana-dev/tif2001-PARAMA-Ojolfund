package com.ojolfund.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojolfund.app.data.Platform
import com.ojolfund.app.data.commissionOptions
import com.ojolfund.app.data.formatRupiah
import com.ojolfund.app.ui.components.AmountField
import com.ojolfund.app.ui.components.CheckRow
import com.ojolfund.app.ui.components.ChoiceChip
import com.ojolfund.app.ui.components.DetailHeader
import com.ojolfund.app.ui.components.IconBox
import com.ojolfund.app.ui.components.OjolCard
import com.ojolfund.app.ui.components.OjolTextField
import com.ojolfund.app.ui.components.Pill
import com.ojolfund.app.ui.components.PrimaryButton
import com.ojolfund.app.ui.components.SelectTile
import com.ojolfund.app.ui.components.toast
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText

private const val DAILY_TRIP_TARGET = 18
private val QUICK_AMOUNTS = listOf(10_000L, 15_000L, 20_000L, 25_000L, 30_000L)
private val PAYMENT_METHODS = listOf("Dompet / Non-Tunai", "Tunai (Cash)")

/** Tambah Order: catat order dari GoRide / Grab / Maxim lengkap dengan komisi dan pendapatan bersih. */
@Composable
fun AddOrderScreen(
    tripsToday: Int,
    onBack: () -> Unit,
    onSave: (
        platform: Platform,
        gross: Long,
        commissionPct: Int,
        route: String,
        method: String,
        note: String
    ) -> Unit
) {
    val context = LocalContext.current
    var platform by remember { mutableStateOf(Platform.GORIDE) }
    var gross by remember { mutableLongStateOf(25_000L) }
    var commissionPct by remember { mutableIntStateOf(Platform.GORIDE.commissionPct) }
    var route by remember { mutableStateOf("") }
    var methodIndex by remember { mutableIntStateOf(0) }
    var note by remember { mutableStateOf("") }
    var addToShift by remember { mutableStateOf(true) }

    val commission = gross * commissionPct / PERCENT
    val net = gross - commission

    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        DetailHeader(title = "Tambah Order", subtitle = "Catat pendapatan per trip", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(OjolColors.Primary))
                    Spacer(Modifier.width(8.dp))
                    Text("Shift Siang Aktif", style = OjolText.LabelLg, color = OjolColors.Primary)
                }
                Pill(
                    "Hari ini, 12:15 WIB",
                    bg = OjolColors.GrayBox2,
                    fg = OjolColors.TextVariant,
                    icon = Icons.Filled.Schedule
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Pilih Platform Mitra", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                Text("Komisi otomatis disesuaikan", style = OjolText.LabelMd, color = OjolColors.Primary)
            }
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Platform.entries.forEach { p ->
                    ChoiceChip(
                        text = p.label,
                        selected = p == platform,
                        dot = true,
                        onClick = {
                            platform = p
                            commissionPct = p.commissionPct
                        },
                        modifier = Modifier.testTag("order_platform_${p.name}")
                    )
                }
            }

            OjolCard {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Tarif Penumpang (Kotor)",
                        style = OjolText.TitleMd,
                        color = OjolColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Filled.TwoWheeler,
                        contentDescription = null,
                        tint = OjolColors.Primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))
                AmountField(value = gross, onValueChange = { gross = it }, testTag = "order_gross")
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QUICK_AMOUNTS.forEach { add ->
                        Text(
                            "+${add / 1_000}rb",
                            style = OjolText.LabelLg,
                            color = OjolColors.TextPrimary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(OjolColors.GrayBox2)
                                .clickable { gross += add }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            OjolCard {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Potongan Komisi Aplikasi",
                        style = OjolText.TitleMd,
                        color = OjolColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Pill("$commissionPct% Terpilih", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep)
                }
                Spacer(Modifier.height(10.dp))
                commissionOptions.chunked(2).forEach { rowOptions ->
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowOptions.forEach { opt ->
                            SelectTile(
                                title = opt.title,
                                subtitle = opt.subtitle,
                                selected = commissionPct == opt.pct,
                                onClick = { commissionPct = opt.pct },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(OjolColors.AquaSoft)
                    .padding(16.dp)
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Percent,
                        contentDescription = null,
                        tint = OjolColors.ExpenseText,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Potongan Platform ($commissionPct%)",
                        style = OjolText.BodyMd,
                        color = OjolColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(formatRupiah(-commission), style = OjolText.TitleMd, color = OjolColors.ExpenseText)
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("MASUK KANTONG BERSIH", style = OjolText.LabelMd, color = OjolColors.PrimaryDeep)
                        Text("Pendapatan Riil Trip Ini", style = OjolText.BodySm, color = OjolColors.TextVariant)
                    }
                    Text(
                        formatRupiah(net),
                        style = OjolText.HeadlineLg.copy(fontSize = 28.sp, fontWeight = FontWeight.Bold),
                        color = OjolColors.TextPrimary,
                        modifier = Modifier.testTag("order_net")
                    )
                }
            }

            OjolCard {
                OjolTextField(
                    label = "Rute Perjalanan (Opsional)",
                    value = route,
                    onValueChange = { route = it },
                    placeholder = "Contoh: Stasiun Tebet → Menara Mandiri",
                    leadingIcon = Icons.Filled.Navigation,
                    testTag = "order_route"
                )
                Spacer(Modifier.height(12.dp))
                Text("Metode Pembayaran", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PAYMENT_METHODS.forEachIndexed { i, label ->
                        SelectTile(
                            title = label,
                            subtitle = "",
                            selected = methodIndex == i,
                            onClick = { methodIndex = i },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                OjolTextField(
                    label = "Catatan Tambahan (Opsional)",
                    value = note,
                    onValueChange = { note = it },
                    placeholder = "Contoh: Tip Rp3.000, parkir ditanggung penumpang",
                    leadingIcon = Icons.Filled.Edit,
                    testTag = "order_note"
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(OjolColors.GrayBox)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBox(Icons.Filled.CheckCircle, size = 44.dp, radius = 12.dp, bg = OjolColors.GrayBox2)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "Target Harian: $tripsToday/$DAILY_TRIP_TARGET Trip",
                        style = OjolText.TitleMd,
                        color = OjolColors.TextPrimary
                    )
                    Text(
                        "Order ini menambah saldo siap tarik hari ini.",
                        style = OjolText.BodySm,
                        color = OjolColors.TextVariant
                    )
                }
            }
        }

        Surface(color = OjolColors.Card, shadowElevation = 8.dp) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                CheckRow(checked = addToShift, onCheckedChange = { addToShift = it }) {
                    Text(
                        "Otomatis tambahkan ke shift aktif",
                        style = OjolText.BodyMd,
                        color = OjolColors.TextPrimary
                    )
                }
                Spacer(Modifier.height(10.dp))
                PrimaryButton(
                    text = "Simpan Order (+${formatRupiah(net)})",
                    leadingIcon = Icons.Filled.CheckCircle,
                    onClick = {
                        if (gross <= 0) {
                            context.toast("Isi tarif penumpang dulu")
                        } else {
                            onSave(platform, gross, commissionPct, route, PAYMENT_METHODS[methodIndex], note)
                            context.toast("Order ${platform.label} tersimpan")
                        }
                    },
                    confirmOnClick = gross > 0,
                    modifier = Modifier.testTag("order_save")
                )
            }
        }
    }
}

private const val PERCENT = 100
