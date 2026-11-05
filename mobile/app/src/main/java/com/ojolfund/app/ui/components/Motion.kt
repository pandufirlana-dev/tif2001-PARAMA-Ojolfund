package com.ojolfund.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ojolfund.app.data.formatNumber

// Komponen animasi kecil yang dipakai bersama oleh banyak layar.

private const val PULSE_RING_MILLIS = 1600
private const val PULSE_BLINK_MILLIS = 800
private const val PULSE_MIN_ALPHA = 0.3f
private const val PRESS_SCALE = 0.96f
private const val PRESS_STIFFNESS = 700f
private const val PRESS_DAMPING = 0.6f
private const val COUNT_UP_MILLIS = 900

/**
 * Titik status yang berdenyut: titik inti menyala-redup terus-menerus, ditambah cincin tipis yang
 * melebar lalu memudar (gaya indikator "live"). Digambar di fase draw sehingga tidak memicu recomposition.
 */
@Composable
fun PulsingDot(color: Color, modifier: Modifier = Modifier, size: Dp = 6.dp) {
    val transition = rememberInfiniteTransition(label = "pulsingDot")
    val ring by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(PULSE_RING_MILLIS, easing = LinearEasing)),
        label = "ring"
    )
    val blink by transition.animateFloat(
        initialValue = 1f,
        targetValue = PULSE_MIN_ALPHA,
        animationSpec = infiniteRepeatable(
            animation = tween(PULSE_BLINK_MILLIS, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink"
    )
    Box(
        modifier
            .size(size)
            .drawBehind {
                val core = this.size.minDimension / 2f
                drawCircle(color = color.copy(alpha = (1f - ring) * 0.45f), radius = core + core * 1.4f * ring)
                drawCircle(color = color.copy(alpha = blink), radius = core)
            }
    )
}

/** Skala yang mengecil sedikit saat elemen ditekan lalu memantul kembali. Pakai bersama [pressScale]. */
@Composable
fun rememberPressScale(source: InteractionSource, pressed: Float = PRESS_SCALE): State<Float> {
    val isPressed by source.collectIsPressedAsState()
    return animateFloatAsState(
        targetValue = if (isPressed) pressed else 1f,
        animationSpec = spring(dampingRatio = PRESS_DAMPING, stiffness = PRESS_STIFFNESS),
        label = "pressScale"
    )
}

/** Menerapkan skala dari [rememberPressScale] tanpa memicu recomposition tiap frame. */
fun Modifier.pressScale(scale: State<Float>): Modifier = graphicsLayer {
    scaleX = scale.value
    scaleY = scale.value
}

private val RUPIAH_REGEX = Regex("^([+-]?)Rp([0-9.]+)(.*)$")

/**
 * Teks nominal Rupiah yang berhitung naik dari 0 (atau dari nilai sebelumnya) ke nilai akhir.
 * Teks yang bukan format "Rp125.000" ditampilkan apa adanya.
 */
@Composable
fun CountUpText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    val match = remember(text) { RUPIAH_REGEX.matchEntire(text) }
    if (match == null) {
        Text(text, style = style, color = color, modifier = modifier, maxLines = maxLines, overflow = overflow)
        return
    }
    val sign = match.groupValues[1]
    val target = match.groupValues[2].replace(".", "").toLongOrNull() ?: 0L
    val suffix = match.groupValues[3]

    val animated = remember { Animatable(0f) }
    LaunchedEffect(target) {
        animated.animateTo(target.toFloat(), tween(COUNT_UP_MILLIS, easing = FastOutSlowInEasing))
    }
    val shown = if (animated.value == target.toFloat()) target else animated.value.toLong()
    Text(
        "${sign}Rp${formatNumber(shown)}$suffix",
        style = style,
        color = color,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow
    )
}
