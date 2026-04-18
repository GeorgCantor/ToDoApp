package com.example.todoapp.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todoapp.R
import com.example.todoapp.presentation.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerScreen(
    navController: NavController,
    viewModel: ThemeViewModel,
) {
    val colors =
        listOf(
            Color(0xFF6200EE),
            Color(0xFF03DAC5),
            Color(0xFFFF5722),
            Color(0xFF4CAF50),
            Color(0xFF2196F3),
            Color(0xFFE91E63),
            Color(0xFF9C27B0),
            Color(0xFF00BCD4),
            Color(0xFFFFEB3B),
            Color(0xFF795548),
        )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.select_theme_color)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(colors.size) { index ->
                val color = colors[index]
                Card(
                    modifier =
                        Modifier
                            .size(100.dp)
                            .clickable {
                                viewModel.updateThemeColor(color.toArgb())
                                navController.popBackStack()
                            },
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = color),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                }
            }
        }
    }
}
