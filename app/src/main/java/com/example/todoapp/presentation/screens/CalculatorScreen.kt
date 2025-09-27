package com.example.todoapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.todoapp.presentation.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    navController: NavController,
    viewModel: CalculatorViewModel,
) {
    val calculatorState by viewModel.calculatorState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Calculation Error") },
            text = { Text(errorMessage.orEmpty()) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            },
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (calculatorState.isScientificMode) {
                            "Scientific Calculator"
                        } else {
                            "Basic Calculator"
                        },
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 60.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            CalculatorDisplay(
                displayValue = calculatorState.displayValue,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(0.2f),
            )

            if (calculatorState.memory != 0.0) {
                Text(
                    text = "M: ${calculatorState.memory}",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            CalculatorKeyboard(
                onButtonClick = viewModel::onButtonClick,
                isScientificMode = calculatorState.isScientificMode,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(2.8f),
            )
        }
    }
}

@Composable
fun CalculatorDisplay(
    displayValue: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .padding(horizontal = 16.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                ),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Text(
            text = displayValue,
            style =
                MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Light,
                ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(horizontal = 16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
        )
    }
}

@Composable
fun CalculatorKeyboard(
    onButtonClick: (String) -> Unit,
    isScientificMode: Boolean,
    modifier: Modifier = Modifier,
) {
    val basicButtons =
        listOf(
            listOf("C", "<", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "="),
        )

    val scientificButtons =
        listOf(
            listOf("π", "e", "^", "√"),
            listOf("sin", "cos", "tan", "!"),
            listOf("log", "ln", "M+", "M-"),
            listOf("MR", "MC", "(", ")"),
        )

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CalculatorButton(
                text = if (isScientificMode) "BASIC" else "SCI",
                onClick = { onButtonClick("SCI") },
                modifier =
                    Modifier
                        .height(46.dp)
                        .weight(1f),
                isFunction = true,
            )
        }

        if (isScientificMode) {
            scientificButtons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    row.forEach { button ->
                        CalculatorButton(
                            text = button,
                            onClick = { onButtonClick(button) },
                            isOperation = button in listOf("÷", "×", "-", "+", "=", "^", "√"),
                            isFunction =
                                button in
                                    listOf(
                                        "C",
                                        "<",
                                        "%",
                                        "sin",
                                        "cos",
                                        "tan",
                                        "log",
                                        "ln",
                                        "!",
                                        "π",
                                        "e",
                                        "M+",
                                        "M-",
                                        "MR",
                                        "MC",
                                    ),
                        )
                    }
                }
            }
        }

        basicButtons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val weight =
                    if (row.size == 4) {
                        1f
                    } else if (row.first() == "0") {
                        2f
                    } else {
                        1f
                    }

                row.forEach { button ->
                    CalculatorButton(
                        text = button,
                        onClick = { onButtonClick(button) },
                        modifier = Modifier.weight(weight),
                        isOperation = button in listOf("÷", "×", "-", "+", "="),
                        isFunction = button in listOf("C", "<", "%"),
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOperation: Boolean = false,
    isFunction: Boolean = false,
) {
    val buttonColors =
        when {
            isOperation ->
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                )

            isFunction ->
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                )

            else ->
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
        }

    Button(
        onClick = onClick,
        modifier =
            modifier
                .padding(4.dp),
        colors = buttonColors,
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp,
        )
    }
}
