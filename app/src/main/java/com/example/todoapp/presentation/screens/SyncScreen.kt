package com.example.todoapp.presentation.screens

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todoapp.presentation.viewmodel.SyncViewModel

@Composable
fun SyncScreen(viewModel: SyncViewModel) {
    val status by viewModel.connectionStatus.collectAsState()
    val messages by viewModel.messages.collectAsState()
    var text by remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Статус: $status", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.startAdvertising() }) { Text("Рекламировать") }
            Button(onClick = { viewModel.startDiscovery() }) { Text("Искать") }
            Button(onClick = { viewModel.stopP2P() }) { Text("Остановить") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Сообщение") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    viewModel.sendMessage(text)
                    text = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Отправить")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Сообщения:", style = MaterialTheme.typography.titleSmall)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(messages) { message ->
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = message.text, style = MaterialTheme.typography.bodyLarge)

                        Row {
                            Text("От: ${message.sender}", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.weight(1F))
                            if (message.synced) {
                                Text("✓", color = MaterialTheme.colorScheme.primary)
                            } else {
                                Text("⌛", color = MaterialTheme.colorScheme.secondary)
                            }
                        }

                        Text(
                            text = DateFormat.format("HH:mm:ss", message.timestamp).toString(),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}
