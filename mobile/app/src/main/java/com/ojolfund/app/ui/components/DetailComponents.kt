package com.ojolfund.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ojolfund.app.data.formatRupiah
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText

/** Header layar detail: tombol kembali, judul, subjudul, dan aksi opsional di kanan. */
@Composable
fun DetailHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Surface(color = OjolColors.Card, shadowElevation = 2.dp, modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(OjolColors.GrayBox)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = OjolColors.TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = OjolText.HeadlineMd, color = OjolColors.TextPrimary, maxLines = 1)
                if (subtitle != null) {
                    Text(subtitle, style = OjolText.BodySm, color = OjolColors.TextVariant, maxLines = 1)
                }
            }
            if (trailing != null) {
                Spacer(Modifier.width(8.dp))
                trailing()
            }
        }
    }
}

/** Chip pilihan (platform, filter, metode bayar). Terpilih = hijau penuh. */
@Composable
fun ChoiceChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dot: Boolean = false
) {
    val bg = if (selected) OjolColors.Primary else OjolColors.Card
    val fg = if (selected) Color.White else OjolColors.TextPrimary
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(bg)
            .border(BorderStroke(1.dp, if (selected) OjolColors.Primary else OjolColors.Border), CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (dot) {
            Box(
                Modifier.size(
                    8.dp
                ).clip(CircleShape).background(if (selected) Color.White else OjolColors.TextSecondary)
            )
        }
        Text(text, style = OjolText.LabelLg, color = fg, maxLines = 1)
    }
}

/** Kotak pilihan persegi (jenis BBM, komisi, metode bayar) dengan judul dan subjudul. */
@Composable
fun SelectTile(title: String, subtitle: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bg = if (selected) OjolColors.Primary else OjolColors.GrayBox
    val fg = if (selected) Color.White else OjolColors.TextPrimary
    val sub = if (selected) Color.White.copy(alpha = 0.85f) else OjolColors.TextVariant
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(title, style = OjolText.TitleLg, color = fg)
        Text(subtitle, style = OjolText.BodySm, color = sub)
    }
}

/** Input nominal rupiah besar. Hanya angka; ikon hapus menghapus satu digit terakhir. */
@Composable
fun AmountField(value: Long, onValueChange: (Long) -> Unit, modifier: Modifier = Modifier, testTag: String? = null) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(OjolColors.GrayBox)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Rp", style = OjolText.TitleLg, color = OjolColors.Primary)
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f)) {
            if (value == 0L) {
                Text("0", style = OjolText.HeadlineLg.copy(fontSize = 28.sp), color = OjolColors.TextSecondary)
            }
            BasicTextField(
                value = if (value == 0L) "" else value.toString(),
                onValueChange = { raw ->
                    onValueChange(raw.filter { it.isDigit() }.take(MAX_DIGITS).toLongOrNull() ?: 0L)
                },
                singleLine = true,
                textStyle = OjolText.HeadlineLg.copy(fontSize = 28.sp, color = OjolColors.TextPrimary),
                cursorBrush = SolidColor(OjolColors.Primary),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.Backspace,
            contentDescription = "Hapus digit terakhir",
            tint = OjolColors.TextSecondary,
            modifier = Modifier
                .size(24.dp)
                .clickable { onValueChange(value / 10) }
        )
    }
}

private const val MAX_DIGITS = 9

/** Dialog kecil untuk memasukkan satu nominal (tambah simpanan, tarik dana, dst). */
@Composable
fun AmountDialog(
    title: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
    hint: String? = null
) {
    var amount by remember { mutableLongStateOf(0L) }
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = OjolColors.Card) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(title, style = OjolText.TitleLg, color = OjolColors.TextPrimary)
                if (hint != null) Text(hint, style = OjolText.BodyMd, color = OjolColors.TextVariant)
                AmountField(value = amount, onValueChange = { amount = it }, testTag = "amount_dialog_input")
                if (amount > 0) {
                    Text(formatRupiah(amount), style = OjolText.LabelMd, color = OjolColors.Primary)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SoftButton(
                        text = "Batal",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        fg = OjolColors.TextVariant
                    )
                    PrimaryButton(
                        text = confirmLabel,
                        onClick = { onConfirm(amount) },
                        modifier = Modifier.weight(1f),
                        height = 48.dp
                    )
                }
            }
        }
    }
}

/** Baris centang dengan teks (pengganti Checkbox bawaan agar warna seragam). */
@Composable
fun CheckRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (checked) OjolColors.Primary else OjolColors.Card)
                .border(
                    BorderStroke(1.5.dp, if (checked) OjolColors.Primary else OjolColors.TextSecondary),
                    RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (checked) Text("✓", color = Color.White, fontSize = 14.sp)
        }
        Box(Modifier.weight(1f)) { content() }
    }
}

/** Kotak tinggi tetap untuk jarak vertikal kecil. */
@Composable
fun VGap(height: Int) {
    Spacer(Modifier.height(height.dp))
}
