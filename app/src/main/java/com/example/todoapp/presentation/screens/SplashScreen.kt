package com.example.todoapp.presentation.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isLoading: Boolean,
    onLoaded: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
    )
    val logoRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(8000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
    )

    val appName = stringResource(id = R.string.app_name)
    val animatedText = remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        appName.forEachIndexed { index, _ ->
            delay(50L * index)
            animatedText.value = appName.substring(0, index + 1)
        }
    }
    val textAlpha by animateFloatAsState(
        targetValue = if (animatedText.value.length == appName.length) 1f else 0.6f,
        animationSpec = tween(400),
    )

    val spinnerProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        spinnerProgress.animateTo(
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
        )
    }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            delay(1500)
            onLoaded()
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    brush =
                        Brush.radialGradient(
                            colors =
                                listOf(
                                    Color(0xFF1A237E),
                                    Color(0xFF0D47A1),
                                    Color(0xFF000000),
                                ),
                            center =
                                Offset(
                                    x = LocalConfiguration.current.screenWidthDp.dp.value / 2,
                                    y = LocalConfiguration.current.screenHeightDp.dp.value / 2,
                                ),
                            radius = Float.POSITIVE_INFINITY,
                        ),
                ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.minDimension * 0.3f
            for (i in 0..2) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.1f),
                    radius = radius + (System.currentTimeMillis() % 2000) / 2000f * 50f,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "App logo",
                modifier =
                    Modifier
                        .size(160.dp)
                        .scale(logoScale)
                        .rotate(logoRotation),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = animatedText.value,
                style =
                    TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    ),
                modifier = Modifier.alpha(textAlpha),
            )

            Spacer(modifier = Modifier.height(32.dp))

            CustomSpinner(progress = spinnerProgress.value)
        }
    }
}

@Composable
fun CustomSpinner(progress: Float) {
    val angle = 360f * progress
    Canvas(modifier = Modifier.size(48.dp)) {
        drawArc(
            color = Color(0xFF42A5F5),
            startAngle = angle,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
        )
        drawArc(
            color = Color(0xFF90CAF9),
            startAngle = angle + 135f,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
        )
    }
}
