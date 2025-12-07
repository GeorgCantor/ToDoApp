package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.model.GameSate
import com.example.todoapp.domain.model.GameStatus
import com.example.todoapp.domain.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(GameSate())
    val gameState = _gameState.asStateFlow()

    fun makeMove(
        row: Int,
        col: Int,
    ) {
        viewModelScope.launch {
            val current = _gameState.value
            if (current.status != GameStatus.PLAYING || current.board[row][col] != Player.NONE) {
                return@launch
            }

            val newBoard =
                current.board.mapIndexed { i, players ->
                    if (i == row) {
                        players.mapIndexed { j, player ->
                            if (j == col) current.currentPlayer else player
                        }
                    } else {
                        players
                    }
                }

            val (winStatus, winningLine) = checkWin(newBoard, row, col)

            _gameState.value =
                current.copy(
                    board = newBoard,
                    currentPlayer = if (current.currentPlayer == Player.X) Player.O else Player.X,
                    status = winStatus ?: if (isBoardFull(newBoard)) GameStatus.DRAW else GameStatus.PLAYING,
                    winningLine = winningLine ?: emptyList(),
                    moveHistory = current.moveHistory + (row to col),
                )
        }
    }

    fun restartGame() {
        viewModelScope.launch { _gameState.value = GameSate() }
    }

    fun undoMove() {
        viewModelScope.launch {
            val current = _gameState.value
            if (current.moveHistory.isEmpty()) return@launch
            val newHistory = current.moveHistory.dropLast(1)
            val newBoard = List(3) { List(3) { Player.NONE } }

            val restoredBoard =
                newHistory.foldIndexed(newBoard) { i, board, (row, col) ->
                    val player = if (i % 2 == 0) Player.X else Player.O
                    board.mapIndexed { r, rowList ->
                        if (r == row) {
                            rowList.mapIndexed { c, current ->
                                if (c == col) player else current
                            }
                        } else {
                            rowList
                        }
                    }
                }

            _gameState.value =
                GameSate(
                    board = restoredBoard,
                    currentPlayer = if (newHistory.size % 2 == 0) Player.X else Player.O,
                    moveHistory = newHistory,
                )
        }
    }

    private fun checkWin(
        board: List<List<Player>>,
        row: Int,
        col: Int,
    ): Pair<GameStatus?, List<Pair<Int, Int>>?> {
        val player = board[row][col]
        if (player == Player.NONE) return null to null

        if (board[row].all { it == player }) {
            return Pair(
                if (player == Player.X) GameStatus.X_WON else GameStatus.O_WON,
                listOf(row to 0, row to 1, row to 2),
            )
        }

        if (board.all { it[col] == player }) {
            return Pair(
                if (player == Player.X) GameStatus.X_WON else GameStatus.O_WON,
                listOf(0 to col, 1 to col, 2 to col),
            )
        }

        if (row == col && board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return Pair(
                if (player == Player.X) GameStatus.X_WON else GameStatus.O_WON,
                listOf(0 to 0, 1 to 1, 2 to 2),
            )
        }

        if (row + col == 2 && board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return Pair(
                if (player == Player.X) GameStatus.X_WON else GameStatus.O_WON,
                listOf(0 to 2, 1 to 1, 2 to 0),
            )
        }

        return null to null
    }

    private fun isBoardFull(board: List<List<Player>>) = board.all { it.all { it != Player.NONE } }
}
