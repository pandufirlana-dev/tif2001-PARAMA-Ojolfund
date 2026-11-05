package com.ojolfund.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojolfund.app.ui.theme.OjolText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Splash selalu hijau (tidak mengikuti mode gelap) agar sama dengan splash sistem.
private val SplashGreen = Color(0xFF087F78)
private val SplashGreenDeep = Color(0xFF075B58)

private const val RING_MILLIS = 2000
private const val TITLE_DELAY = 350L
private const val TAGLINE_DELAY = 250L
private const val HOLD_MILLIS = 900L
private const val LOGO_SPRING_DAMPING = 0.55f

/**
 * Splash animasi: logo memantul masuk di dalam cincin yang melebar, nama dan tagline menyusul.
 * Warna latar sama dengan splash sistem (lihat `themes.xml`) sehingga peralihannya mulus.
 * [onFinished] dipanggil sekali setelah animasi selesai.
 */
@Composable
fun SplashScreen(onFinished: () -> Unit, modifier: Modifier = Modifier) {
    val finished by rememberUpdatedState(onFinished)
    val logo = remember { Animatable(0f) }
    val title = remember { Animatable(0f) }
    val tagline = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { logo.animateTo(1f, spring(dampingRatio = LOGO_SPRING_DAMPING, stiffness = Spring.StiffnessLow)) }
        delay(TITLE_DELAY)
        launch { title.animateTo(1f, tween(500, easing = FastOutSlowInEasing)) }
        delay(TAGLINE_DELAY)
        launch { tagline.animateTo(1f, tween(500, easing = FastOutSlowInEasing)) }
        delay(HOLD_MILLIS)
        finished()
    }

    val rings = rememberInfiniteTransition(label = "splashRings")
    val phase by rings.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(RING_MILLIS, easing = LinearEasing)),
        label = "phase"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SplashGreen, SplashGreenDeep))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(260.dp)) {
                    val base = 52.dp.toPx()
                    val spread = 78.dp.toPx()
                    for (offset in listOf(0f, 0.5f)) {
                        val t = (phase + offset) % 1f
                        drawCircle(
                            color = Color.White.copy(alpha = (1f - t) * 0.28f * logo.value.coerceIn(0f, 1f)),
                            radius = base + spread * t,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .graphicsLayer {
                            val s = 0.5f + 0.5f * logo.value
                            scaleX = s
                            scaleY = s
                            alpha = logo.value.coerceIn(0f, 1f)
                        }
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.AccountBalanceWallet,
                        contentDescription = "OjolFund",
                        tint = SplashGreen,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }
            Text(
                "OjolFund",
                style = OjolText.Display.copy(fontSize = 34.sp, fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.graphicsLayer {
                    alpha = title.value
                    translationY = (1f - title.value) * 24.dp.toPx()
                }
            )
            Text(
                "Buku Kas Mitra Driver",
                style = OjolText.BodyLg,
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.graphicsLayer {
                    alpha = tagline.value
                    translationY = (1f - tagline.value) * 16.dp.toPx()
                }
            )
        }
    }
}
