package com.ojolfund.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.ojolfund.app.data.formatNumber
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText
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
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(Unit) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
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

// ---------------------------------------------------------------- Haptic

/** Getaran halus untuk ketukan biasa (tombol, tab). Mengembalikan fungsi yang bisa dipanggil dari event klik. */
@Composable
fun rememberTapHaptic(): () -> Unit {
    val haptic = LocalHapticFeedback.current
    return remember(haptic) { { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) } }
}

// ---------------------------------------------------------------- Skeleton shimmer

private const val SHIMMER_MILLIS = 1200
private const val SHIMMER_SPAN = 400f
private const val SKELETON_MILLIS = 700L
private val seenSkeletonKeys = mutableSetOf<String>()

/** Kotak placeholder abu-abu dengan kilau yang bergerak, dipakai sebagai kerangka saat data dimuat. */
@Composable
fun ShimmerBox(modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(12.dp)) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shift by transition.animateFloat(
        initialValue = -SHIMMER_SPAN,
        targetValue = SHIMMER_SPAN * 3,
        animationSpec = infiniteRepeatable(tween(SHIMMER_MILLIS, easing = LinearEasing)),
        label = "shift"
    )
    val base = OjolColors.GrayBox2
    val glow = OjolColors.GrayBox
    Box(
        modifier
            .clip(shape)
            .background(base)
            .drawBehind {
                drawRect(
                    Brush.linearGradient(
                        colors = listOf(Color.Transparent, glow, Color.Transparent),
                        start = Offset(shift, 0f),
                        end = Offset(shift + SHIMMER_SPAN, 0f)
                    )
                )
            }
    )
}

/**
 * True selama kerangka harus tampil. Hanya aktif saat [key] pertama kali dibuka sejak aplikasi dijalankan,
 * jadi berpindah tab bolak-balik tidak memunculkan kerangka lagi.
 * Ganti dengan status pemuatan nyata ketika data sudah berasal dari server.
 */
@Composable
fun rememberSkeletonLoading(key: String, millis: Long = SKELETON_MILLIS): Boolean {
    var loading by remember { mutableStateOf(key !in seenSkeletonKeys) }
    LaunchedEffect(key) {
        if (loading) {
            delay(millis)
            seenSkeletonKeys.add(key)
            loading = false
        }
    }
    return loading
}

// ---------------------------------------------------------------- Pull to refresh

private const val REFRESH_MILLIS = 900L
private const val PULL_RESISTANCE = 0.5f
private const val PULL_THRESHOLD_DP = 72
private const val PULL_HOLD_DP = 56
private const val INDICATOR_SIZE_DP = 40
private const val INDICATOR_HIDDEN_DP = 48

/**
 * Menarik layar ke bawah untuk menyegarkan (tanpa API eksperimental Material3).
 * Saat ini hanya simulasi (jeda singkat lalu [onRefreshed]), karena data masih lokal;
 * hubungkan ke sinkronisasi server saat backend siap.
 */
@Composable
fun OjolRefreshBox(
    modifier: Modifier = Modifier,
    onRefreshed: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    val scope = rememberCoroutineScope()
    val haptic = rememberTapHaptic()
    val density = LocalDensity.current
    val thresholdPx = with(density) { PULL_THRESHOLD_DP.dp.toPx() }
    val holdPx = with(density) { PULL_HOLD_DP.dp.toPx() }
    var refreshing by remember { mutableStateOf(false) }
    var offset by remember { mutableFloatStateOf(0f) }
    val latestRefreshed by rememberUpdatedState(onRefreshed)

    val connection = remember(thresholdPx, holdPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Menggulung balik ke atas saat indikator masih tertarik: habiskan dulu tarikannya.
                if (refreshing || available.y >= 0f || offset <= 0f) return Offset.Zero
                val consumed = maxOf(available.y, -offset)
                offset += consumed
                return Offset(0f, consumed)
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                // Sisa tarikan ke bawah setelah daftar sudah di posisi paling atas.
                if (refreshing || source != NestedScrollSource.UserInput || available.y <= 0f) return Offset.Zero
                offset += available.y * PULL_RESISTANCE
                return Offset(0f, available.y)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (refreshing) return Velocity.Zero
                if (offset >= thresholdPx) {
                    refreshing = true
                    haptic()
                    scope.launch {
                        animate(offset, holdPx) { value, _ -> offset = value }
                        delay(REFRESH_MILLIS)
                        animate(offset, 0f) { value, _ -> offset = value }
                        refreshing = false
                        latestRefreshed()
                    }
                } else if (offset > 0f) {
                    scope.launch { animate(offset, 0f) { value, _ -> offset = value } }
                }
                return Velocity.Zero
            }
        }
    }

    Box(modifier.nestedScroll(connection)) {
        Box(
            Modifier.fillMaxSize().graphicsLayer { translationY = offset },
            content = content
        )
        if (offset > 0f || refreshing) {
            val progress = (offset / thresholdPx).coerceIn(0f, 1f)
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .size(INDICATOR_SIZE_DP.dp)
                    .graphicsLayer {
                        translationY = offset - INDICATOR_HIDDEN_DP.dp.toPx()
                        alpha = if (refreshing) 1f else progress
                    }
                    .shadow(2.dp, CircleShape)
                    .background(OjolColors.Card, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (refreshing) {
                    CircularProgressIndicator(
                        color = OjolColors.Primary,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    CircularProgressIndicator(
                        progress = { progress },
                        color = OjolColors.Primary,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------- Empty state

private const val FLOAT_MILLIS = 1800
private const val FLOAT_DISTANCE = 8f

/** Keadaan kosong: ikon melayang pelan di atas lingkaran berdenyut, dengan judul dan keterangan. */
@Composable
fun AnimatedEmptyState(icon: ImageVector, title: String, subtitle: String, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "empty")
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(FLOAT_MILLIS, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bob"
    )
    Column(
        modifier.fillMaxWidth().padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(Modifier.size(96.dp), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .size(84.dp)
                    .graphicsLayer {
                        val s = 0.9f + 0.1f * bob
                        scaleX = s
                        scaleY = s
                    }
                    .background(OjolColors.Aqua, CircleShape)
            )
            Icon(
                icon,
                contentDescription = null,
                tint = OjolColors.PrimaryDeep,
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer { translationY = (0.5f - bob) * 2f * FLOAT_DISTANCE.dp.toPx() }
            )
        }
        Text(title, style = OjolText.TitleMd, color = OjolColors.TextPrimary, textAlign = TextAlign.Center)
        Text(subtitle, style = OjolText.BodyMd, color = OjolColors.TextVariant, textAlign = TextAlign.Center)
    }
}
