package com.example.todoapp.presentation.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    isLoading: Boolean,
    onLoaded: () -> Unit,
) {
    val logoAlpha = remember { Animatable(0f) }
    val textScale = remember { Animatable(0.5f) }
    val progressRotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
            )
        }
        launch {
            textScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = LinearEasing),
            )
        }
        launch {
            progressRotation.animateTo(
                targetValue = 360f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(1500, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart,
                    ),
            )
        }
    }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            delay(1000)
            onLoaded()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "App logo",
                modifier =
                    Modifier
                        .size(128.dp)
                        .alpha(logoAlpha.value),
            )
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 26.sp),
                modifier = Modifier.scale(textScale.value),
            )
            CircularProgressIndicator(
                modifier =
                    Modifier
                        .padding(top = 24.dp)
                        .size(40.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.LightGray,
                strokeWidth = 3.dp,
            )
        }
    }
}
