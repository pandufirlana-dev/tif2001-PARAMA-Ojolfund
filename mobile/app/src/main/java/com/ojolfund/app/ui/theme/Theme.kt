package com.ojolfund.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Token warna dari DESIGN.md "Nusantara Driver Ledger", dengan varian gelap.
 * Tiap warna dibaca lewat getter sehingga otomatis mengikuti [isDark]; karena [isDark] adalah state Compose,
 * semua layar yang membaca warna ini ikut tergambar ulang saat tema berganti.
 */
object OjolColors {
    /** Diatur oleh [OjolfundTheme] sesuai pengaturan sistem. */
    var isDark by mutableStateOf(false)

    private fun pick(light: Long, dark: Long) = Color(if (isDark) dark else light)

    val Primary get() = pick(0xFF087F78, 0xFF14998F)
    val PrimaryDeep get() = pick(0xFF075B58, 0xFF8FE3DC)
    val PrimaryDark = Color(0xFF00645E)
    val Emerald get() = pick(0xFF16A394, 0xFF2BC4B2)
    val Mint get() = pick(0xFFEAF6F3, 0xFF16302D)
    val Aqua get() = pick(0xFFA7ECE8, 0xFF1D4B47)
    val AquaSoft get() = pick(0xFFC9F3EF, 0xFF1A403D)
    val Canvas get() = pick(0xFFF5F7F7, 0xFF0F1514)
    val Card get() = pick(0xFFFFFFFF, 0xFF1A2220)
    val TextPrimary get() = pick(0xFF181C1C, 0xFFE7ECEB)
    val TextSecondary get() = pick(0xFF7B8584, 0xFF9AA5A3)
    val TextVariant get() = pick(0xFF3E4947, 0xFFB5C2BF)
    val Border get() = pick(0xFFE7ECEB, 0xFF2A3432)
    val GrayBox get() = pick(0xFFF1F4F4, 0xFF222B29)
    val GrayBox2 get() = pick(0xFFE6E9E8, 0xFF2D3735)
    val Expense get() = pick(0xFFE65C65, 0xFFF0757D)
    val ExpenseText get() = pick(0xFFB3141B, 0xFFFF9A9F)
    val ExpenseBg get() = pick(0xFFFDECEC, 0xFF3A2224)
    val Warning get() = pick(0xFFE8A23B, 0xFFF0B25A)
    val WarningBg get() = pick(0xFFFEF6EC, 0xFF3A2D1A)
    val GoRideBg get() = pick(0xFFE8F8EE, 0xFF173326)
    val GoRideText get() = pick(0xFF00880C, 0xFF5BD66A)
    val GrabBg get() = pick(0xFFEAF8ED, 0xFF173326)
    val GrabText get() = pick(0xFF00B14F, 0xFF4BD688)
    val MaximBg get() = pick(0xFFFEF8E7, 0xFF3A3318)
    val MaximText get() = pick(0xFFC97A00, 0xFFF0B04A)
    val ShiftRed get() = pick(0xFFBA1A1A, 0xFFFF6B6B)

    /** Latar lembut untuk kartu/gradien bertema hijau (dulu E3FAF6, E6F8F5, E3EFEE). */
    val Wash get() = pick(0xFFE3FAF6, 0xFF15302E)
    val WashStrong get() = pick(0xFF9CF1E8, 0xFF1B4A46)

    /** Abu-abu untuk titik/garis tidak aktif (dulu BDC9C7). */
    val Inactive get() = pick(0xFFBDC9C7, 0xFF4B5957)
}

/** Skala tipografi (Inter di desain; di sini memakai font sistem agar tanpa aset tambahan). */
object OjolText {
    val Display =
        TextStyle(fontSize = 32.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.9).sp)
    val HeadlineLg =
        TextStyle(fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp)
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

/** Skema Material dibangun dari token OjolColors pada saat tema diterapkan (terang atau gelap). */
private fun ojolColorScheme(dark: Boolean): ColorScheme = if (dark) {
    darkColorScheme(
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
        error = OjolColors.ShiftRed
    )
} else {
    lightColorScheme(
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
        error = OjolColors.ShiftRed
    )
}

@Composable
fun OjolfundTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    // Setel sebelum konten dikomposisi agar semua pembacaan warna memakai palet yang benar.
    if (OjolColors.isDark != darkTheme) OjolColors.isDark = darkTheme
    val scheme = ojolColorScheme(darkTheme)
    MaterialTheme(colorScheme = scheme, content = content)
}
