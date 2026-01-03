package com.example.todoapp.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.domain.model.StatsUiState
import com.example.todoapp.presentation.viewmodel.SpaceXStatsViewModel
import com.example.todoapp.presentation.visualization.VisualizationResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceXStatsScreen(viewModel: SpaceXStatsViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val visualizations by viewModel.visualizations.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadStatistics()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Статистика SpaceX") },
                actions = {
                    IconButton(onClick = { viewModel.loadStatistics() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                },
            )
        },
    ) { paddingValues ->
        when (uiState) {
            StatsUiState.Loading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            StatsUiState.Success -> {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(
                        items = visualizations,
                        key = { it.title },
                    ) {
                        VisualizationCard(it)
                    }
                }
            }

            is StatsUiState.Error -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            text = "Ошибка загрузки статистики",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Text((uiState as StatsUiState.Error).message)
                        Button(onClick = { viewModel.loadStatistics() }) {
                            Text("Повторить")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VisualizationCard(result: VisualizationResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = result.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text("Данные: ${result.data}")
        }
    }
}
