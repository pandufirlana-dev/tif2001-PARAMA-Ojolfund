package com.ojolfund.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Token warna dari DESIGN.md "Nusantara Driver Ledger". */
object OjolColors {
    val Primary = Color(0xFF087F78)
    val PrimaryDeep = Color(0xFF075B58)
    val PrimaryDark = Color(0xFF00645E)
    val Emerald = Color(0xFF16A394)
    val Mint = Color(0xFFEAF6F3)
    val Aqua = Color(0xFFA7ECE8)
    val AquaSoft = Color(0xFFC9F3EF)
    val Canvas = Color(0xFFF5F7F7)
    val Card = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFF181C1C)
    val TextSecondary = Color(0xFF7B8584)
    val TextVariant = Color(0xFF3E4947)
    val Border = Color(0xFFE7ECEB)
    val GrayBox = Color(0xFFF1F4F4)
    val GrayBox2 = Color(0xFFE6E9E8)
    val Expense = Color(0xFFE65C65)
    val ExpenseText = Color(0xFFB3141B)
    val ExpenseBg = Color(0xFFFDECEC)
    val Warning = Color(0xFFE8A23B)
    val WarningBg = Color(0xFFFEF6EC)
    val GoRideBg = Color(0xFFE8F8EE)
    val GoRideText = Color(0xFF00880C)
    val GrabBg = Color(0xFFEAF8ED)
    val GrabText = Color(0xFF00B14F)
    val MaximBg = Color(0xFFFEF8E7)
    val MaximText = Color(0xFFC97A00)
    val ShiftRed = Color(0xFFBA1A1A)
}

/** Skala tipografi (Inter di desain; di sini memakai font sistem agar tanpa aset tambahan). */
object OjolText {
    val Display = TextStyle(fontSize = 32.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.9).sp)
    val HeadlineLg = TextStyle(fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp)
    val HeadlineMd = TextStyle(fontSize = 20.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold)
    val TitleLg = TextStyle(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold)
    val TitleMd = TextStyle(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold)
    val BodyLg = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.Normal)
    val BodyMd = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal)
    val BodySm = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Normal)
    val LabelLg = TextStyle(fontSize = 14.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold)
    val LabelMd = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold)
    val LabelSm = TextStyle(fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.4.sp)
}

private val OjolColorScheme = lightColorScheme(
    primary = OjolColors.Primary,
    onPrimary = Color.White,
    primaryContainer = OjolColors.Aqua,
    onPrimaryContainer = OjolColors.PrimaryDeep,
    secondary = OjolColors.Emerald,
    background = OjolColors.Canvas,
    onBackground = OjolColors.TextPrimary,
    surface = OjolColors.Card,
    onSurface = OjolColors.TextPrimary,
    surfaceVariant = OjolColors.GrayBox,
    onSurfaceVariant = OjolColors.TextVariant,
    outline = OjolColors.Border,
    error = OjolColors.ShiftRed,
)

@Composable
fun OjolfundTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = OjolColorScheme, content = content)
}
