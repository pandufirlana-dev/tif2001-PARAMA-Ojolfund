package com.ojolfund.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ojolfund.app.data.formatNumber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Komponen animasi kecil yang dipakai bersama oleh banyak layar.

private const val PULSE_RING_MILLIS = 1600
private const val PULSE_BLINK_MILLIS = 800
private const val PULSE_MIN_ALPHA = 0.3f
private const val PRESS_SCALE = 0.96f
private const val PRESS_STIFFNESS = 700f
private const val PRESS_DAMPING = 0.6f
private const val COUNT_UP_MILLIS = 900
private const val STAGGER_STEP_MILLIS = 70L
private const val STAGGER_MAX_INDEX = 8
private const val STAGGER_MILLIS = 380
private const val GROW_MILLIS = 700

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

/**
 * Membungkus [content] agar muncul bertahap: memudar masuk sambil naik sedikit.
 * [index] menentukan jeda (tiap item 70 ms setelah sebelumnya, maksimal 8 item) sehingga daftar terasa mengalir.
 */
@Composable
fun StaggerIn(index: Int, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(index.coerceIn(0, STAGGER_MAX_INDEX) * STAGGER_STEP_MILLIS)
        progress.animateTo(1f, tween(STAGGER_MILLIS, easing = FastOutSlowInEasing))
    }
    Box(
        modifier.graphicsLayer {
            alpha = progress.value
            translationY = (1f - progress.value) * 14.dp.toPx()
        }
    ) {
        content()
    }
}

/** Nilai 0..1 yang tumbuh sekali saat layar tampil (setelah [delayMillis]); dipakai batang grafik dan bar progres. */
@Composable
fun rememberGrow(delayMillis: Int = 0, durationMillis: Int = GROW_MILLIS): State<Float> {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        progress.animateTo(1f, tween(durationMillis, easing = FastOutSlowInEasing))
    }
    return progress.asState()
}

/** Memperlebar elemen dari kiri ke kanan sesuai [progress] (untuk bar bersegmen). */
fun Modifier.growX(progress: State<Float>): Modifier = graphicsLayer {
    scaleX = progress.value
    transformOrigin = TransformOrigin(0f, 0.5f)
}

/**
 * Lencana centang sukses: lingkaran membesar dengan pantulan, lalu tanda centang tergambar bertahap.
 */
@Composable
fun AnimatedCheckBadge(
    modifier: Modifier = Modifier,
    size: Dp = 30.dp,
    background: Color = Color(0xFF087F78),
    checkColor: Color = Color.White
) {
    val pop = remember { Animatable(0f) }
    val draw = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        launch { pop.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow)) }
        delay(CHECK_DELAY_MILLIS)
        draw.animateTo(1f, tween(CHECK_DRAW_MILLIS, easing = FastOutSlowInEasing))
    }
    Canvas(
        modifier
            .size(size)
            .graphicsLayer {
                scaleX = pop.value
                scaleY = pop.value
            }
    ) {
        drawCircle(color = background)
        val w = this.size.width
        val check = Path().apply {
            moveTo(w * 0.28f, w * 0.52f)
            lineTo(w * 0.44f, w * 0.68f)
            lineTo(w * 0.74f, w * 0.36f)
        }
        val measure = PathMeasure()
        measure.setPath(check, false)
        val partial = Path()
        measure.getSegment(0f, measure.length * draw.value, partial, true)
        drawPath(
            path = partial,
            color = checkColor,
            style = Stroke(width = w * 0.09f, cap = StrokeCap.Round)
        )
    }
}

private const val CHECK_DELAY_MILLIS = 180L
private const val CHECK_DRAW_MILLIS = 380
