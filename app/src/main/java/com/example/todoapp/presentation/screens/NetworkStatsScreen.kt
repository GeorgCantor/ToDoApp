package com.example.todoapp.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todoapp.domain.model.NetworkMetrics
import com.example.todoapp.presentation.viewmodel.NetworkStatsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkStatsScreen(viewModel: NetworkStatsViewModel) {
    val metrics by viewModel.metrics.collectAsState()
    var expandedItemId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Network Statistics") },
                actions = {
                    IconButton(onClick = { viewModel.clearHistory() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Summary", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total requests: ${metrics.size}")
                    Text("Success rate: ${(viewModel.getSuccessRate() * 100).toInt()}%")
                    Text("Errors: ${viewModel.getErrorCount()}")
                    Text("Avg total time: ${viewModel.getAverageTotalTime()} ms")
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(metrics) { metric ->
            NetworkMetricItem(
                metric = metric,
                expanded = expandedItemId == metric.id,
                onToggle = {
                    expandedItemId = if (expandedItemId == metric.id) null else metric.id
                },
            )
        }
    }
}

@Composable
fun NetworkMetricItem(
    metric: NetworkMetrics,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onToggle() },
        colors =
            CardDefaults.cardColors(
                containerColor = if (metric.success) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.errorContainer,
            ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = metric.method,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        text = metric.url.take(50),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${metric.totalDurationMs} ms",
                        fontWeight = FontWeight.Bold,
                        color = if (metric.totalDurationMs > 3000) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    )
                    metric.responseCode?.let {
                        Text(text = "HTTP $it", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow("DNS", metric.dnsDurationMs)
                DetailRow("Connect", metric.connectDurationMs)
                DetailRow("TLS", metric.tlsDurationMs)
                DetailRow("Request Headers", metric.requestHeadersDurationMs)
                DetailRow("Request Body", metric.requestBodyDurationMs)
                DetailRow("Response Headers", metric.responseHeadersDurationMs)
                DetailRow("Response Body", metric.responseBodyDurationMs)
                metric.errorMessage?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Error: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: Long?,
) {
    if (value != null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall)
            Text(text = "$value ms", style = MaterialTheme.typography.bodySmall)
        }
    }
}
