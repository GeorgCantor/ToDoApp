package com.example.todoapp.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.todoapp.domain.model.Maze
import com.example.todoapp.presentation.viewmodel.MazeGameViewModel

@Composable
fun MazeGameScreen(viewModel: MazeGameViewModel) {
    val gameState by viewModel.gameState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> viewModel.startGame()
                    Lifecycle.Event.ON_PAUSE -> {}
                    else -> Unit
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MazeCanvas(
            maze = gameState.maze,
            ballX = gameState.ballX,
            ballY = gameState.ballY,
            isFinished = gameState.isFinished,
            modifier = Modifier.fillMaxSize(),
        )

        Button(
            onClick = { viewModel.resetGame() },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        ) {
            Text(if (gameState.isFinished) "Сыграть ещё" else "Сброс")
        }

        if (gameState.isFinished) {
            Text(
                text = "ПОБЕДА!",
                color = Color.White,
                modifier = Modifier.align(Alignment.TopCenter).padding(16.dp),
            )
        }
    }
}

@Composable
fun MazeCanvas(
    maze: Maze,
    ballX: Float,
    ballY: Float,
    isFinished: Boolean,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val cellWidth = size.width / maze.width
        val cellHeight = size.height / maze.height

        for (y in 0 until maze.height) {
            for (x in 0 until maze.width) {
                val left = x * cellWidth
                val top = y * cellHeight

                drawRect(
                    color = if (maze.isWall(x, y)) Color.DarkGray else Color.LightGray,
                    topLeft = Offset(left, top),
                    size = Size(cellWidth, cellHeight),
                )

                if (maze.isFinish(x, y)) {
                    drawRect(
                        color = Color.Green,
                        topLeft = Offset(left, top),
                        size = Size(cellWidth, cellHeight),
                    )
                }
            }
        }

        val ballRadius = minOf(cellWidth, cellHeight) * 0.35f
        val ballCenterX = ballX * cellWidth
        val ballCenterY = ballY * cellHeight

        drawCircle(
            color = if (isFinished) Color.Green else Color.Red,
            radius = ballRadius,
            center = Offset(ballCenterX, ballCenterY),
        )
    }
}
