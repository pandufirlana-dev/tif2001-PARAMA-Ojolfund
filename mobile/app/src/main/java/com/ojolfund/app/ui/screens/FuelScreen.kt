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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ojolfund.app.data.FuelType
import com.ojolfund.app.data.VehicleData
import com.ojolfund.app.data.formatDecimal
import com.ojolfund.app.data.formatNumber
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
import com.ojolfund.app.ui.components.ProgressLine
import com.ojolfund.app.ui.components.SelectTile
import com.ojolfund.app.ui.components.toast
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText

private val FUEL_METHODS = listOf("Tunai", "QRIS / GoPay", "Debit Card")
private val QUICK_FUEL = listOf(10_000L, 20_000L, 30_000L)
private const val FULL_TANK_AMOUNT = 50_000L

/**
 * Catat BBM: jenis bahan bakar, nominal, odometer, efisiensi, SPBU, dan metode bayar.
 * Setelah disimpan muncul dialog konfirmasi (Konfirmasi Catat BBM); [onDone] dipanggil saat ditutup.
 */
@Composable
fun FuelScreen(
    onBack: () -> Unit,
    onSave: (fuelLabel: String, liters: Double, amount: Long, method: String, station: String) -> Unit,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    var fuel by remember { mutableStateOf(FuelType.PERTALITE) }
    var customPrice by remember { mutableLongStateOf(11_000L) }
    var amount by remember { mutableLongStateOf(30_000L) }
    var odometerText by remember { mutableStateOf("") }
    var odometerError by remember { mutableStateOf<String?>(null) }
    var station by remember { mutableStateOf("SPBU Pertamina 34-12401 Margonda Raya") }
    var methodIndex by remember { mutableIntStateOf(0) }
    var addToShift by remember { mutableStateOf(true) }
    var note by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

    val price = if (fuel == FuelType.LAINNYA) customPrice else fuel.pricePerLiter
    val liters = if (price > 0) amount.toDouble() / price else 0.0
    val odometer = odometerText.toLongOrNull()
    val distance = if (odometer != null &&
        odometer > VehicleData.LAST_ODOMETER
    ) {
        odometer - VehicleData.LAST_ODOMETER
    } else {
        null
    }
    val kmPerLiter = if (distance != null && liters > 0) distance / liters else null
    val costPerKm = if (distance != null && distance > 0) amount / distance else null

    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        DetailHeader(title = "Catat BBM", subtitle = "Isi bensin kilat", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OjolCard {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconBox(Icons.Filled.TwoWheeler, size = 44.dp, radius = 12.dp, bg = OjolColors.GrayBox2)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(VehicleData.NAME, style = OjolText.TitleMd, color = OjolColors.TextPrimary)
                        Text(VehicleData.PLATE, style = OjolText.BodySm, color = OjolColors.TextVariant)
                    }
                    Pill("AKTIF", bg = OjolColors.Aqua, fg = OjolColors.PrimaryDeep)
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(OjolColors.GrayBox)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Odo Terakhir: ${formatNumber(VehicleData.LAST_ODOMETER)} km",
                        style = OjolText.BodySm,
                        color = OjolColors.TextVariant
                    )
                    Text(VehicleData.LAST_FILL, style = OjolText.BodySm, color = OjolColors.TextVariant)
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Jenis Bahan Bakar", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                Text(
                    "Update Harga Resmi",
                    style = OjolText.LabelMd,
                    color = OjolColors.Primary,
                    modifier = Modifier.clickable { context.toast("Harga resmi diperbarui (demo)") }
                )
            }
            FuelType.entries.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    pair.forEach { type ->
                        SelectTile(
                            title = type.label,
                            subtitle = type.ron + " • " +
                                if (type.pricePerLiter >
                                    0
                                ) {
                                    formatRupiah(type.pricePerLiter) + " /L"
                                } else {
                                    "Kustom Harga"
                                },
                            selected = fuel == type,
                            onClick = { fuel = type },
                            modifier = Modifier.weight(1f).testTag("fuel_type_${type.name}")
                        )
                    }
                }
            }
            if (fuel == FuelType.LAINNYA) {
                OjolCard {
                    Text("Harga per Liter", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                    Spacer(Modifier.height(6.dp))
                    AmountField(value = customPrice, onValueChange = { customPrice = it }, testTag = "fuel_price")
                }
            }

            OjolCard {
                Text("Nominal Biaya Bensin", style = OjolText.TitleMd, color = OjolColors.TextPrimary)
                Spacer(Modifier.height(10.dp))
                AmountField(value = amount, onValueChange = { amount = it }, testTag = "fuel_amount")
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QUICK_FUEL.forEach { add ->
                        ChoiceChip(
                            text = "+Rp ${add / 1_000}rb",
                            selected = amount == add,
                            onClick = { amount += add }
                        )
                    }
                    ChoiceChip(
                        text = "Full (Rp ${FULL_TANK_AMOUNT / 1_000}rb)",
                        selected = amount == FULL_TANK_AMOUNT,
                        onClick = { amount = FULL_TANK_AMOUNT }
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(OjolColors.AquaSoft)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconBox(Icons.Filled.LocalGasStation, size = 40.dp, radius = 20.dp, bg = OjolColors.Aqua)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Volume Otomatis", style = OjolText.BodySm, color = OjolColors.TextVariant)
                        Text(
                            "${formatDecimal(liters, 2)} Liter",
                            style = OjolText.HeadlineMd.copy(fontWeight = FontWeight.Bold),
                            color = OjolColors.TextPrimary,
                            modifier = Modifier.testTag("fuel_liters")
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "${formatRupiah(amount)} ÷ ${formatRupiah(price)}",
                            style = OjolText.BodySm,
                            color = OjolColors.TextVariant
                        )
                        Text("✔ Presisi SPBU", style = OjolText.LabelMd, color = OjolColors.Primary)
                    }
                }
            }

            OjolCard {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Speed,
                        contentDescription = null,
                        tint = OjolColors.Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Angka Odometer Saat Ini",
                        style = OjolText.TitleMd,
                        color = OjolColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text("Satuan KM", style = OjolText.BodySm, color = OjolColors.TextVariant)
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(OjolColors.GrayBox)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.weight(1f)) {
                        if (odometerText.isEmpty()) {
                            Text(
                                formatNumber(VehicleData.LAST_ODOMETER + 100).replace(".", ""),
                                style = OjolText.HeadlineMd,
                                color = OjolColors.TextSecondary
                            )
                        }
                        BasicTextField(
                            value = odometerText,
                            onValueChange = {
                                odometerText = it.filter { c -> c.isDigit() }.take(MAX_ODO_DIGITS)
                                odometerError = null
                            },
                            singleLine = true,
                            textStyle = OjolText.HeadlineMd.copy(color = OjolColors.TextPrimary),
                            cursorBrush = SolidColor(OjolColors.Primary),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("fuel_odometer")
                        )
                    }
                    Text("KM", style = OjolText.TitleMd, color = OjolColors.TextVariant)
                }
                if (odometerError != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        odometerError.orEmpty(),
                        style = OjolText.BodySm,
                        color = OjolColors.ExpenseText,
                        modifier = Modifier.testTag("fuel_odometer_error")
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "Odo Lalu: ${formatNumber(VehicleData.LAST_ODOMETER)} km",
                        style = OjolText.BodySm,
                        color = OjolColors.TextVariant
                    )
                    if (distance != null) {
                        Pill(
                            "+${formatNumber(distance)} km sejak isi lalu",
                            bg = OjolColors.GrayBox2,
                            fg = OjolColors.PrimaryDeep
                        )
                    }
                }
                if (kmPerLiter != null && costPerKm != null) {
                    Spacer(Modifier.height(10.dp))
                    EfficiencyBox(kmPerLiter = kmPerLiter, costPerKm = costPerKm)
                }
            }

            OjolCard {
                OjolTextField(
                    label = "Lokasi SPBU",
                    value = station,
                    onValueChange = { station = it },
                    leadingIcon = Icons.Filled.Edit,
                    testTag = "fuel_station"
                )
                Spacer(Modifier.height(12.dp))
                Text("Metode Pembayaran", style = OjolText.LabelLg, color = OjolColors.TextPrimary)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FUEL_METHODS.forEachIndexed { i, label ->
                        ChoiceChip(text = label, selected = methodIndex == i, onClick = { methodIndex = i })
                    }
                }
            }

            OjolCard(onClick = { context.toast("Kamera & OCR struk (demo)") }) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Foto Struk SPBU", style = OjolText.TitleMd, color = OjolColors.TextPrimary)
                    Text("Opsional", style = OjolText.BodySm, color = OjolColors.TextVariant)
                }
                Spacer(Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(OjolColors.GrayBox)
                        .padding(vertical = 18.dp, horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconBox(Icons.Filled.CheckCircle, size = 48.dp, radius = 24.dp, bg = OjolColors.Aqua)
                    Spacer(Modifier.height(8.dp))
                    Text("Ambil Foto / Scan Struk", style = OjolText.TitleMd, color = OjolColors.TextPrimary)
                    Text(
                        "Mendukung deteksi otomatis (OCR) nominal & literan. Tetap tersimpan saat offline.",
                        style = OjolText.BodySm,
                        color = OjolColors.TextVariant
                    )
                }
            }

            OjolCard {
                CheckRow(checked = addToShift, onCheckedChange = { addToShift = it }) {
                    Column {
                        Text(
                            "Beban Operasional Shift Berjalan",
                            style = OjolText.LabelLg,
                            color = OjolColors.TextPrimary
                        )
                        Text(
                            "Biaya ${formatRupiah(amount)} akan otomatis memotong pendapatan kotor shift hari ini.",
                            style = OjolText.BodySm,
                            color = OjolColors.TextVariant
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                OjolTextField(
                    label = "Catatan Tambahan (Opsional)",
                    value = note,
                    onValueChange = { note = it },
                    placeholder = "Cth: Isi sebelum on-bid jam sibuk sore"
                )
            }
        }

        Surface(color = Color.White, shadowElevation = 8.dp) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                PrimaryButton(
                    text = "Simpan Catatan BBM",
                    leadingIcon = Icons.Filled.CheckCircle,
                    onClick = {
                        when {
                            amount <= 0 -> context.toast("Isi nominal biaya bensin dulu")
                            price <= 0 -> context.toast("Isi harga per liter dulu")
                            odometer == null -> odometerError = "Isi angka odometer saat ini"
                            odometer <= VehicleData.LAST_ODOMETER ->
                                odometerError =
                                    "Odometer harus lebih besar dari ${formatNumber(VehicleData.LAST_ODOMETER)} km"
                            else -> {
                                onSave(fuel.label, liters, amount, FUEL_METHODS[methodIndex], station)
                                saved = true
                            }
                        }
                    },
                    modifier = Modifier.testTag("fuel_save")
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Cloud,
                        contentDescription = null,
                        tint = OjolColors.TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Tersimpan otomatis ke database lokal (Mode Offline Siap)",
                        style = OjolText.BodySm.copy(fontSize = 11.sp),
                        color = OjolColors.TextSecondary
                    )
                }
            }
        }
    }

    if (saved) {
        FuelSavedDialog(onClose = onDone)
    }
}

private const val MAX_ODO_DIGITS = 7

@Composable
private fun EfficiencyBox(kmPerLiter: Double, costPerKm: Long) {
    val standard = VehicleData.STANDARD_KM_PER_LITER
    val diffPct = (kmPerLiter - standard) / standard * 100
    val good = kmPerLiter >= standard
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(OjolColors.GrayBox)
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Efisiensi Bahan Bakar",
                style = OjolText.LabelLg,
                color = OjolColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Pill(
                if (good) "Sangat Irit 🌿" else "Agak Boros",
                bg = if (good) OjolColors.Aqua else OjolColors.WarningBg,
                fg = if (good) OjolColors.PrimaryDeep else OjolColors.MaximText
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            EfficiencyCell("Konsumsi BBM", formatDecimal(kmPerLiter, 1), "km/L", Modifier.weight(1f))
            EfficiencyCell("Biaya per Kilometer", formatRupiah(costPerKm), "/km", Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        ProgressLine(progress = (kmPerLiter / (standard * 1.25)).toFloat(), color = OjolColors.Primary)
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "Standar Vario: ${formatDecimal(standard, 0)} km/L",
                style = OjolText.BodySm,
                color = OjolColors.TextVariant
            )
            Text(
                (if (diffPct >= 0) "+" else "") + formatDecimal(diffPct, 1) + "% " +
                    if (good) "Lebih Efisien" else "Kurang Efisien",
                style = OjolText.LabelMd,
                color = if (good) OjolColors.Primary else OjolColors.ExpenseText
            )
        }
    }
}

@Composable
private fun EfficiencyCell(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Text(label, style = OjolText.BodySm, color = OjolColors.TextVariant)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = OjolText.HeadlineMd.copy(fontWeight = FontWeight.Bold), color = OjolColors.TextPrimary)
            Spacer(Modifier.width(4.dp))
            Text(unit, style = OjolText.BodySm, color = OjolColors.TextVariant)
        }
    }
}

/** Konfirmasi Catat BBM: muncul setelah data tersimpan. */
@Composable
private fun FuelSavedDialog(onClose: () -> Unit) {
    Dialog(onDismissRequest = onClose) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier.width(40.dp).height(4.dp).clip(CircleShape).background(OjolColors.GrayBox2)
                )
                Spacer(Modifier.height(18.dp))
                Box(contentAlignment = Alignment.BottomEnd) {
                    IconBox(
                        Icons.Filled.LocalGasStation,
                        size = 84.dp,
                        radius = 42.dp,
                        bg = OjolColors.Aqua,
                        tint = OjolColors.PrimaryDeep,
                        iconSize = 40.dp
                    )
                    IconBox(
                        Icons.Filled.CheckCircle,
                        size = 30.dp,
                        radius = 15.dp,
                        bg = OjolColors.Primary,
                        tint = Color.White,
                        iconSize = 18.dp
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "Catatan BBM Berhasil Disimpan!",
                    style = OjolText.HeadlineLg,
                    color = OjolColors.TextPrimary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.testTag("fuel_saved_title")
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Data BBM & odometer telah dicatat ke pembukuan shift berjalan.",
                    style = OjolText.BodyMd,
                    color = OjolColors.TextVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Pill(
                    "Offline Tersimpan ✓",
                    bg = OjolColors.GrayBox2,
                    fg = OjolColors.PrimaryDeep,
                    icon = Icons.Filled.Cloud
                )
                Spacer(Modifier.height(18.dp))
                PrimaryButton(text = "Selesai", onClick = onClose, modifier = Modifier.testTag("fuel_saved_done"))
            }
        }
    }
}
