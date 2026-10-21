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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojolfund.app.data.ExpenseCategory
import com.ojolfund.app.data.formatRupiah
import com.ojolfund.app.ui.components.AvatarCircle
import com.ojolfund.app.ui.components.OjolCard
import com.ojolfund.app.ui.components.OjolLogo
import com.ojolfund.app.ui.components.Pill
import com.ojolfund.app.ui.components.PrimaryButton
import com.ojolfund.app.ui.components.toast
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText
import java.util.Locale

private val presets = listOf(5_000L, 10_000L, 25_000L, 50_000L)

@Composable
fun AddExpenseScreen(onBack: () -> Unit, onSave: (amount: Long, category: ExpenseCategory, note: String) -> Unit) {
    val context = LocalContext.current
    var amount by remember { mutableStateOf(25_000L) }
    var category by remember { mutableStateOf(ExpenseCategory.BBM) }
    var note by remember { mutableStateOf("") }

    val amountText = if (amount == 0L) "" else formatRupiah(amount).removePrefix("Rp")
    val liters = amount / 10_000.0

    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(OjolColors.GrayBox)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = OjolColors.TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            OjolLogo(size = 36.dp)
            Spacer(Modifier.width(8.dp))
            Text(
                "Catat Pengeluaran",
                style = OjolText.TitleLg.copy(fontWeight = FontWeight.Bold),
                color = OjolColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
            AvatarCircle(size = 40.dp)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Petunjuk
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(34.dp).clip(CircleShape).background(OjolColors.Aqua),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Bolt,
                        contentDescription = null,
                        tint = OjolColors.PrimaryDeep,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    "Catat pengeluaran operasional narik kilat",
                    style = OjolText.BodyMd,
                    color = OjolColors.TextVariant,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Pill("3-Tap Mode", bg = OjolColors.GrayBox2, fg = OjolColors.TextPrimary, dot = true, textSize = 12.sp)
            }

            // Nominal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(Color.White, Color.White, Color(0xFFE6F8F5))))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Nominal Biaya", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Center) {
                    Text(
                        "Rp",
                        style = OjolText.HeadlineMd.copy(fontSize = 24.sp),
                        color = OjolColors.Primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    BasicTextField(
                        value = amountText,
                        onValueChange = { raw ->
                            val digits = raw.filter { it.isDigit() }.take(9)
                            amount = digits.toLongOrNull() ?: 0L
                        },
                        singleLine = true,
                        textStyle = OjolText.Display.copy(
                            fontSize = 48.sp,
                            lineHeight = 56.sp,
                            color = OjolColors.TextPrimary,
                            textAlign = TextAlign.Start
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        cursorBrush = SolidColor(OjolColors.Primary),
                        modifier = Modifier.widthIn(min = 24.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    presets.forEach { p ->
                        val selected = amount == p
                        Text(
                            formatRupiah(p),
                            style = OjolText.LabelLg,
                            color = if (selected) OjolColors.PrimaryDeep else OjolColors.TextPrimary,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) OjolColors.Aqua else OjolColors.GrayBox)
                                .clickable { amount = p }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }

            // Kategori
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Pilih Kategori Pos",
                    style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                    color = OjolColors.TextPrimary
                )
                Text("Tap 1x", style = OjolText.BodyMd, color = OjolColors.TextVariant)
            }
            val cats = ExpenseCategory.entries
            cats.chunked(3).forEach { rowCats ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowCats.forEach { c ->
                        CategoryCard(
                            category = c,
                            selected = c == category,
                            modifier = Modifier.weight(1f),
                            onClick = { category = c }
                        )
                    }
                }
            }

            // Kalkulasi BBM + SPBU (hanya untuk BBM)
            if (category == ExpenseCategory.BBM) {
                OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), padding = 16.dp) {
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
                                Icons.Filled.LocalGasStation,
                                contentDescription = null,
                                tint = OjolColors.Primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Text("Kalkulasi Otomatis BBM", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                        }
                        Pill("Pertalite Subsidi", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep, textSize = 11.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(OjolColors.GrayBox)
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Patokan Harga", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                            Text(
                                "Rp 10.000 / Liter",
                                style = OjolText.TitleMd.copy(fontWeight = FontWeight.SemiBold),
                                color = OjolColors.TextPrimary
                            )
                        }
                        Box(Modifier.width(1.dp).height(44.dp).background(OjolColors.Border))
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Estimasi Didapat", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                            Text(
                                "~" + String.format(Locale.US, "%.1f", liters) + " Liter",
                                style = OjolText.HeadlineMd.copy(fontWeight = FontWeight.Bold),
                                color = OjolColors.Primary
                            )
                        }
                    }
                }
            }

            // Waktu transaksi
            OjolCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), padding = 14.dp) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(OjolColors.GrayBox),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = OjolColors.TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Waktu Transaksi", style = OjolText.BodyMd, color = OjolColors.TextVariant)
                        Text(
                            "Hari ini, 24 Okt 2024 • 10:15 WIB",
                            style = OjolText.TitleMd.copy(fontWeight = FontWeight.Bold),
                            color = OjolColors.TextPrimary
                        )
                    }
                    Box(
                        Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(OjolColors.GrayBox)
                            .clickable { context.toast("Ubah waktu transaksi (demo)") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.EditCalendar,
                            contentDescription = "Ubah waktu",
                            tint = OjolColors.TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            if (category == ExpenseCategory.BBM) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF8A2B1F), Color(0xFF3C2A28), Color(0xFF1D1F1F)))
                        )
                ) {
                    Icon(
                        Icons.Filled.LocalGasStation,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.14f),
                        modifier = Modifier.size(140.dp).align(Alignment.CenterEnd).padding(end = 16.dp)
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Navigation,
                            contentDescription = null,
                            tint = OjolColors.AquaSoft,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "SPBU Pertamina Terdekat Terdeteksi",
                            style = OjolText.LabelLg,
                            color = Color.White,
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )
                        Pill("Akurasi GPS", bg = Color.White.copy(alpha = 0.22f), fg = Color.White, textSize = 11.sp)
                    }
                }
            }

            // Keterangan
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.EditNote,
                    contentDescription = null,
                    tint = OjolColors.TextVariant,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Box(Modifier.weight(1f)) {
                    if (note.isEmpty()) {
                        Text(
                            "Keterangan (misal: SPBU Margonda / Pom ber...",
                            style = OjolText.BodyLg,
                            color = OjolColors.TextSecondary.copy(alpha = 0.7f),
                            maxLines = 1
                        )
                    }
                    BasicTextField(
                        value = note,
                        onValueChange = { note = it.take(60) },
                        singleLine = true,
                        textStyle = OjolText.BodyLg.copy(color = OjolColors.TextPrimary),
                        cursorBrush = SolidColor(OjolColors.Primary),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Simpan
            PrimaryButton(
                text = "Simpan Pengeluaran (-" + formatRupiah(amount).replace("Rp", "Rp ") + ")",
                onClick = {
                    if (amount <= 0L) {
                        context.toast("Isi nominal biaya dulu")
                    } else {
                        context.toast("Pengeluaran tersimpan")
                        onSave(amount, category, note.trim())
                    }
                },
                leadingIcon = Icons.Filled.CheckCircle,
                height = 56.dp
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CategoryCard(
    category: ExpenseCategory,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) OjolColors.Aqua else Color.White)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            Modifier.size(52.dp).clip(CircleShape).background(if (selected) Color.White else OjolColors.GrayBox2),
            contentAlignment = Alignment.Center
        ) {
            val tint = if (selected) OjolColors.PrimaryDeep else OjolColors.TextPrimary
            val icon: ImageVector? = when (category) {
                ExpenseCategory.BBM -> Icons.Filled.LocalGasStation
                ExpenseCategory.PARKIR -> null
                ExpenseCategory.MAKAN -> Icons.Filled.LocalCafe
                ExpenseCategory.DATA -> Icons.Filled.BarChart
                ExpenseCategory.SERVIS -> Icons.Filled.Build
                ExpenseCategory.LAINNYA -> Icons.Filled.MoreHoriz
            }
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(26.dp))
            } else {
                Text("P", color = tint, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        Text(
            category.label,
            style = OjolText.LabelLg,
            color = if (selected) OjolColors.PrimaryDeep else OjolColors.TextPrimary,
            maxLines = 1
        )
    }
}
