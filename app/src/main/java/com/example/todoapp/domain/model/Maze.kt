package com.example.todoapp.domain.model

data class Maze(
    val width: Int = 7,
    val height: Int = 7,
    val walls: Array<IntArray> =
        arrayOf(
            intArrayOf(1, 1, 1, 1, 1, 1, 1),
            intArrayOf(1, 0, 0, 0, 0, 0, 1),
            intArrayOf(1, 0, 1, 1, 1, 0, 1),
            intArrayOf(1, 0, 1, 0, 0, 0, 1),
            intArrayOf(1, 0, 1, 1, 1, 0, 1),
            intArrayOf(1, 0, 0, 0, 0, 0, 1),
            intArrayOf(1, 1, 1, 1, 1, 1, 1),
        ),
    val finishX: Int = 5,
    val finishY: Int = 5,
) {
    fun isWall(
        cellX: Int,
        cellY: Int,
    ) = walls[cellY][cellX] == 1

    fun isFinish(
        cellX: Int,
        cellY: Int,
    ) = cellX == finishX && cellY == finishY
}
