package com.example.todoapp.domain.model

data class MazeGameState(
    val ballX: Float,
    val ballY: Float,
    val isFinished: Boolean = false,
    val maze: Maze = Maze(),
)
