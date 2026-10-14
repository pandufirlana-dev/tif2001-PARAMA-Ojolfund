package com.ojolfund.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojolfund.app.data.TagKind
import com.ojolfund.app.ui.theme.OjolColors

data class TagStyle(
    val label: String,
    val bg: Color,
    val fg: Color,
    val icon: ImageVector?,
    val iconBg: Color,
    val iconTint: Color,
)

fun tagStyle(kind: TagKind): TagStyle = when (kind) {
    TagKind.GORIDE -> TagStyle("GoRide", OjolColors.GoRideBg, OjolColors.GoRideText, Icons.Filled.TwoWheeler, OjolColors.GoRideBg, OjolColors.GoRideText)
    TagKind.GRAB -> TagStyle("GrabBike", OjolColors.GrabBg, OjolColors.GrabText, Icons.Filled.Moped, OjolColors.GrabBg, OjolColors.GoRideText)
    TagKind.MAXIM -> TagStyle("Maxim", OjolColors.MaximBg, OjolColors.MaximText, Icons.Filled.Moped, OjolColors.MaximBg, OjolColors.MaximText)
    TagKind.BBM -> TagStyle("BBM", OjolColors.ExpenseBg, OjolColors.ExpenseText, Icons.Filled.LocalGasStation, OjolColors.ExpenseBg, OjolColors.Expense)
    TagKind.PARKIR -> TagStyle("Parkir", OjolColors.ExpenseBg, OjolColors.ExpenseText, null, OjolColors.ExpenseBg, OjolColors.Expense)
    TagKind.MAKAN -> TagStyle("Makan", OjolColors.WarningBg, OjolColors.MaximText, Icons.Filled.Restaurant, OjolColors.ExpenseBg, OjolColors.Expense)
    TagKind.LAINNYA -> TagStyle("Lainnya", OjolColors.GrayBox, OjolColors.TextVariant, Icons.Filled.MoreHoriz, OjolColors.GrayBox, OjolColors.TextVariant)
}

/** Kotak ikon kategori transaksi; "Parkir" memakai huruf P seperti di desain. */
@Composable
fun TagIcon(kind: TagKind, modifier: Modifier = Modifier, size: Dp = 40.dp) {
    val style = tagStyle(kind)
    if (style.icon != null) {
        IconBox(style.icon, modifier = modifier, bg = style.iconBg, tint = style.iconTint, size = size, radius = 10.dp)
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(10.dp))
                .background(style.iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Text("P", color = style.iconTint, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
