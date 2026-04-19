package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.GravityData
import com.example.todoapp.domain.model.Maze
import com.example.todoapp.domain.model.MazeGameState

class GameUseCase {
    private val maze = Maze()
    var state = MazeGameState(ballX = 1.5F, ballY = 1.5F, maze = maze)
        private set

    private var velocityX = 0F
    private var velocityY = 0F

    fun update(
        dtSec: Float,
        gravity: GravityData,
    ) {
        if (state.isFinished) return

        val sensitivity = 12F // чувствительность к наклону
        val friction = 0.98F // трение (чем меньше, тем быстрее тормозит)

        // ускорение от наклона (ось Y инвертирована, чтобы наклон вперёд - шарик вниз)
        val accelX = gravity.x * sensitivity
        val accelY = -gravity.y * sensitivity

        velocityX += accelX * dtSec
        velocityY += accelY * dtSec

        velocityX *= friction
        velocityY *= friction

        var newX = state.ballX + velocityX * dtSec
        var newY = state.ballY + velocityY * dtSec

        // проверка столкновений со стенами
        val radius = 0.4F // радиус шарика в клетках (чтобы не проваливался в стены)

        // проверка по X
        val cellX = newX.toInt()
        val cellY = state.ballY.toInt()
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

        state = state.copy(ballX = newX, ballY = newY, isFinished = finish)
        if (finish) {
            velocityX = 0F
            velocityY = 0F
        }
    }

    fun resetGame() {
        state = MazeGameState(ballX = 1.5F, ballY = 1.5F, maze = maze)
        velocityX = 0F
        velocityY = 0F
    }
}
