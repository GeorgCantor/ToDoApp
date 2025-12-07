package com.example.todoapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.R
import com.example.todoapp.domain.model.GameStatus
import com.example.todoapp.domain.model.Player
import com.example.todoapp.presentation.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicTacToeScreen() {
    val viewModel: GameViewModel = viewModel()
    val gameState = viewModel.gameState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tictactoe_title), fontWeight = FontWeight.Bold) },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.primaryContainer,
                        titleContentColor = colorScheme.onPrimaryContainer,
                    ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GameStatusCard(
                status = gameState.value.status,
                currentPlayer = gameState.value.currentPlayer,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            GameBoard(
                board = gameState.value.board,
                winningLine = gameState.value.winningLine,
                onCellClick = { row, col -> viewModel.makeMove(row, col) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1F)
                        .padding(16.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            GameControls(
                onRestart = { viewModel.restartGame() },
                onUndo = { viewModel.undoMove() },
                canUndo = gameState.value.moveHistory.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )

            Text(
                text = stringResource(R.string.rules),
                modifier = Modifier.padding(16.dp),
                color = colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun GameStatusCard(
    status: GameStatus,
    currentPlayer: Player,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text =
                    when (status) {
                        GameStatus.PLAYING -> {
                            stringResource(if (currentPlayer == Player.X) R.string.current_turn_x else R.string.current_turn_o)
                        }
                        GameStatus.X_WON -> stringResource(R.string.x_wins)
                        GameStatus.O_WON -> stringResource(R.string.o_wins)
                        GameStatus.DRAW -> stringResource(R.string.draw)
                    },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color =
                    when (status) {
                        GameStatus.X_WON -> colorScheme.primary
                        GameStatus.O_WON -> colorScheme.secondary
                        else -> colorScheme.onSurface
                    },
            )

            if (status == GameStatus.PLAYING) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.make_move),
                    fontSize = 14.sp,
                    color = colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun GameBoard(
    board: List<List<Player>>,
    winningLine: List<Pair<Int, Int>>,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(2.dp, colorScheme.outline, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
        ) {
            repeat(3) { row ->
                Row(
                    modifier = Modifier.weight(1F).fillMaxWidth(),
                ) {
                    repeat(3) { col ->
                        val isWinningCell = winningLine.any { it.first == row && it.second == col }
                        Box(
                            modifier =
                                Modifier
                                    .weight(1F)
                                    .fillMaxSize()
                                    .padding(4.dp)
                                    .background(
                                        if (isWinningCell) colorScheme.primary.copy(alpha = 0.1F) else Color.Transparent,
                                        RoundedCornerShape(4.dp),
                                    ).border(
                                        1.dp,
                                        colorScheme.outline.copy(alpha = 0.3F),
                                        RoundedCornerShape(4.dp),
                                    ).clickable { onCellClick(row, col) },
                            contentAlignment = Alignment.Center,
                        ) {
                            when (board[row][col]) {
                                Player.X -> DrawX(isWinningCell)
                                Player.O -> DrawO(isWinningCell)
                                Player.NONE -> {}
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawX(isWinning: Boolean) {
    val color = if (isWinning) Color.Green else Color.Red
    Box(
        modifier =
            Modifier
                .size(58.dp)
                .drawBehind {
                    drawLine(
                        color = color,
                        start = Offset(0F, 0F),
                        end = Offset(size.width, size.height),
                        strokeWidth = 8F,
                    )
                    drawLine(
                        color = color,
                        start = Offset(size.width, 0F),
                        end = Offset(0F, size.height),
                        strokeWidth = 8F,
                    )
                },
    )
}

@Composable
fun DrawO(isWinning: Boolean) {
    val color = if (isWinning) Color.Green else Color.Red
    Box(
        modifier =
            Modifier
                .size(58.dp)
                .drawBehind {
                    drawCircle(
                        color = color,
                        radius = size.minDimension / 2 - 4,
                        style = Stroke(width = 8F),
                    )
                },
    )
}

@Composable
fun GameControls(
    onRestart: () -> Unit,
    onUndo: () -> Unit,
    canUndo: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        IconButton(
            onClick = onUndo,
            enabled = canUndo,
            modifier =
                Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (canUndo) colorScheme.secondaryContainer else colorScheme.surfaceVariant),
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.undo_move),
                tint = if (canUndo) colorScheme.onSecondaryContainer else colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = onRestart,
            modifier = Modifier.height(56.dp).weight(1F),
            shape = RoundedCornerShape(12.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.new_game),
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.new_game), fontSize = 16.sp)
        }
    }
}
