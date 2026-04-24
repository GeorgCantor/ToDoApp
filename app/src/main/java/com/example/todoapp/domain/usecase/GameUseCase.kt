package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.GravityData
import com.example.todoapp.domain.model.Maze
import com.example.todoapp.domain.model.MazeGameState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameUseCase {
    private val maze = Maze()

    private val _state = MutableStateFlow(MazeGameState(ballX = 1.5F, ballY = 1.5F, maze = maze))
    val state: StateFlow<MazeGameState> = _state.asStateFlow()

    private var velocityX = 0F
    private var velocityY = 0F

    fun update(
        dtSec: Float,
        gravity: GravityData,
    ) {
        if (_state.value.isFinished) return

        val sensitivity = 12F // чувствительность к наклону
        val friction = 0.98F // трение (чем меньше, тем быстрее тормозит)

        // ускорение от наклона (ось Y инвертирована, чтобы наклон вперёд - шарик вниз)
        val accelX = gravity.x * sensitivity
        val accelY = -gravity.y * sensitivity

        velocityX += accelX * dtSec
        velocityY += accelY * dtSec

        velocityX *= friction
        velocityY *= friction

        var newX = _state.value.ballX + velocityX * dtSec
        var newY = _state.value.ballY + velocityY * dtSec

        // проверка столкновений со стенами
        val radius = 0.4F // радиус шарика в клетках (чтобы не проваливался в стены)

        // проверка по X
        val cellX = newX.toInt()
        val cellY = _state.value.ballY.toInt()
        val leftWall = maze.isWall(cellX, cellY) || (newX - radius < cellX && cellX > 0 && maze.isWall(cellX - 1, cellY))
        val rightWall = maze.isWall(cellX, cellY) || (newX + radius > cellX + 1 && cellX + 1 < maze.width && maze.isWall(cellX + 1, cellY))
        if (leftWall && newX - radius < cellX) newX = cellX + radius
        if (rightWall && newX + radius > cellX + 1) newX = cellX + 1 - radius

        // проверка по Y
        val cellY2 = newY.toInt()
        val cellX2 = newX.toInt()
        val topWall = maze.isWall(cellX2, cellY2) || (newY - radius < cellY2 && cellY2 > 0 && maze.isWall(cellX2, cellY2 - 1))
        val bottomWall =
            maze.isWall(cellX2, cellY2) || (newY + radius > cellY2 + 1 && cellY2 + 1 < maze.height && maze.isWall(cellX2, cellY2 + 1))
        if (topWall && newY - radius < cellY2) newY = cellY2 + radius
        if (bottomWall && newY + radius > cellY2 + 1) newY = cellY2 + 1 - radius

        // финиш
        val finish = maze.isFinish(newX.toInt(), newY.toInt())

        _state.value = _state.value.copy(ballX = newX, ballY = newY, isFinished = finish)

        if (finish) {
            velocityX = 0F
            velocityY = 0F
        }
    }

    fun resetGame() {
        _state.value = MazeGameState(ballX = 1.5F, ballY = 1.5F, maze = maze)
        velocityX = 0F
        velocityY = 0F
    }
}
