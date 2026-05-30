package com.example.todoapp.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.presentation.viewmodel.DrawingViewModel
import kotlinx.coroutines.launch

@Composable
fun DrawingScreen(viewModel: DrawingViewModel) {
    val strokes by viewModel.strokes.collectAsStateWithLifecycle()
    val currentColor by viewModel.currentColor.collectAsStateWithLifecycle()
    val currentStrokeWidth by viewModel.currentStrokeWidth.collectAsStateWithLifecycle()
    val isEraserMode by viewModel.isEraserMode.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    val colors = listOf(Color.Black, Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta)

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            colors.forEach { color ->
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = color,
                    onClick = { viewModel.setColor(color) },
                    border = if (currentColor == color && !isEraserMode) BorderStroke(2.dp, Color.Black) else null,
                ) {}
            }

            Button(
                onClick = { viewModel.toggleEraserMode() },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = if (isEraserMode) Color.Gray else Color.LightGray,
                    ),
            ) {
                Text("Ластик")
            }

            Button(onClick = { viewModel.clearCanvas() }) {
                Text("Очистить")
            }
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Slider(
                value = currentStrokeWidth,
                onValueChange = { viewModel.setStrokeWidth(it) },
                valueRange = 2f..50f,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "Толщина: ${currentStrokeWidth.toInt()} px",
                modifier = Modifier.padding(start = 16.dp),
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
        ) {
            Canvas(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                                val points = mutableListOf(down.position)
                                var lastPoint = down.position

                                do {
                                    val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                                    val currentPoint = event.changes.firstOrNull()?.position

                                    if (currentPoint != null && currentPoint != lastPoint) {
                                        points.add(currentPoint)
                                        lastPoint = currentPoint

                                        if (points.size >= 2) {
                                            coroutineScope.launch {
                                                viewModel.addStrokeSegment(
                                                    points = listOf(points[points.size - 2], points[points.size - 1]),
                                                )
                                            }
                                        }
                                    }
                                } while (event.changes.any { it.pressed })

                                if (points.size >= 2) {
                                    coroutineScope.launch {
                                        viewModel.addStroke(points)
                                    }
                                }
                            }
                        },
            ) {
                strokes.forEach { stroke ->
                    for (i in 0 until stroke.points.lastIndex) {
                        drawLine(
                            color = stroke.color,
                            start = stroke.points[i],
                            end = stroke.points[i + 1],
                            strokeWidth = stroke.strokeWidth,
                        )
                    }
                }
            }
        }
    }
}
