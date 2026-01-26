package com.example.todoapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.domain.model.CoroutineInfo
import com.example.todoapp.domain.model.CoroutineState
import com.example.todoapp.domain.model.ThreadInfo
import com.example.todoapp.presentation.viewmodel.CoroutineMonitorViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoroutineMonitorScreen(viewModel: CoroutineMonitorViewModel) {
    val monitorData by viewModel.monitorData.collectAsStateWithLifecycle()
    val isMonitoring by viewModel.isMonitoring.collectAsStateWithLifecycle()
    val selectedCoroutineId by viewModel.selectedCoroutineId.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        delay(1000)
        viewModel.createCoroutines()
    }

    val selectedCoroutine = monitorData.activeCoroutines.find { it.id == selectedCoroutineId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coroutine Monitor") },
                actions = {
                    IconButton(onClick = { viewModel.createCoroutines() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = { viewModel.toggleMonitoring() }) {
                        Icon(
                            if (isMonitoring) Icons.Default.Warning else Icons.Default.PlayArrow,
                            contentDescription = if (isMonitoring) "Pause" else "Resume",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            MonitoringStatusCard(isMonitoring = isMonitoring)

            if (monitorData.threads.isNotEmpty()) {
                ThreadsListSection(threads = monitorData.threads)
            }

            CoroutinesListSection(
                coroutines = monitorData.activeCoroutines,
                selectedCoroutineId = selectedCoroutineId,
                onCoroutineClick = { viewModel.selectCoroutine(it) },
            )

            if (selectedCoroutine != null) {
                CoroutineDetailsCard(coroutine = selectedCoroutine)
            }

            SystemInfoCard(
                threadCount = monitorData.threads.size,
                coroutineCount = monitorData.activeCoroutines.size,
            )
        }
    }
}

@Composable
private fun MonitoringStatusCard(isMonitoring: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (isMonitoring) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (isMonitoring) Color.Green else Color.Gray,
                            ),
                )
                Text(
                    text = if (isMonitoring) "Мониторинг активен" else "Мониторинг приостановлен",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Text(
                text = if (isMonitoring) "🟢" else "⏸️",
                fontSize = 20.sp,
            )
        }
    }
}

@Composable
private fun ThreadsListSection(threads: List<ThreadInfo>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Потоки (${threads.size})",
                style = MaterialTheme.typography.titleSmall,
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.height(min(200.dp, (threads.size * 30).dp)),
            ) {
                items(threads) { thread ->
                    ThreadRow(thread = thread)
                }
            }
        }
    }
}

@Composable
private fun SystemInfoCard(
    threadCount: Int,
    coroutineCount: Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "📊 Статистика системы",
                style = MaterialTheme.typography.titleSmall,
            )

            DetailRow(title = "Всего потоков", value = "$threadCount")
            DetailRow(title = "Всего корутин", value = "$coroutineCount")
            DetailRow(
                title = "Активных корутин",
                value = "TODO", // Можно добавить подсчет в ViewModel
            )
            DetailRow(
                title = "Память",
                value = "${Runtime.getRuntime().totalMemory() / 1024 / 1024} MB",
            )
        }
    }
}

@Composable
private fun ThreadRow(thread: ThreadInfo) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        color =
                            when (thread.state) {
                                Thread.State.RUNNABLE -> Color.Green
                                Thread.State.BLOCKED -> Color.Red
                                Thread.State.WAITING -> Color.Yellow
                                Thread.State.TIMED_WAITING -> Color(0xFFFFA500)
                                Thread.State.TERMINATED -> Color.Gray
                                else -> Color.LightGray
                            },
                    ),
        )

        Text(
            text = thread.name.take(20),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = thread.state.toString().replace("Thread.State.", ""),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CoroutinesListSection(
    coroutines: List<CoroutineInfo>,
    selectedCoroutineId: String?,
    onCoroutineClick: (String) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Корутины (${coroutines.size})",
                    style = MaterialTheme.typography.titleSmall,
                )

                if (coroutines.isNotEmpty()) {
                    Text(
                        text = "Нажмите для деталей",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (coroutines.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Нет активных корутин",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(coroutines, key = { it.id }) { coroutine ->
                        CoroutineItem(
                            coroutine = coroutine,
                            isSelected = coroutine.id == selectedCoroutineId,
                            onClick = { onCoroutineClick(coroutine.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CoroutineItem(
    coroutine: CoroutineInfo,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
            ),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(
                            color =
                                when (coroutine.state) {
                                    CoroutineState.ACTIVE -> Color.Green
                                    CoroutineState.SUSPENDED -> Color.Yellow
                                    CoroutineState.CANCELLING -> Color(0xFFFFA500)
                                    CoroutineState.CANCELLED -> Color.Red
                                    CoroutineState.COMPLETED -> Color.Gray
                                },
                        ),
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = coroutine.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = coroutine.dispatcher,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Text(
                        text = "${coroutine.duration}ms",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }

            if (coroutine.children > 0) {
                Box(
                    modifier =
                        Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "👶 ${coroutine.children}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun CoroutineDetailsCard(coroutine: CoroutineInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "📋 Детали корутины",
                style = MaterialTheme.typography.titleMedium,
            )

            DetailRow(title = "ID", value = coroutine.id.take(12))
            DetailRow(title = "Имя", value = coroutine.name)
            DetailRow(title = "Диспетчер", value = coroutine.dispatcher)
            DetailRow(
                title = "Состояние",
                value = getStateText(coroutine.state),
            )
            DetailRow(title = "Время работы", value = "${coroutine.duration}ms")
            DetailRow(title = "Дочерние", value = coroutine.children.toString())
        }
    }
}

@Composable
private fun DetailRow(
    title: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace,
        )
    }
}

private fun getStateText(state: CoroutineState) =
    when (state) {
        CoroutineState.ACTIVE -> "Активна 🟢"
        CoroutineState.SUSPENDED -> "Ожидает 🟡"
        CoroutineState.CANCELLING -> "Отменяется 🟠"
        CoroutineState.CANCELLED -> "Отменена 🔴"
        CoroutineState.COMPLETED -> "Завершена ⚫"
    }
