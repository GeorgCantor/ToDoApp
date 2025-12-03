package com.example.todoapp.domain.model

enum class Player {
    X, O, NONE
}

enum class GameStatus {
    PLAYING, X_WON, O_WON, DRAW
}

data class GameSate(
    val board: List<List<Player>> = List(3) { List(3) { Player.NONE } },
    val currentPlayer: Player = Player.X,
    val status: GameStatus = GameStatus.PLAYING,
    val winningLine: List<Pair<Int, Int>> = emptyList(),
    val moveHistory: List<Pair<Int, Int>> = emptyList(),
)