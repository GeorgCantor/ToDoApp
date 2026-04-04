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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todoapp.presentation.viewmodel.SyncViewModel

@Composable
fun SyncScreen(vieModel: SyncViewModel) {
    val status by vieModel.connectionStatus.collectAsState()
    val messages by vieModel.messages.collectAsState()

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
            Button(onClick = { vieModel.startAdvertising() }) { Text("Рекламировать") }
            Button(onClick = { vieModel.startDiscovery() }) { Text("Искать") }
            Button(onClick = { vieModel.stopP2P() }) { Text("Остановить") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Сообщения:", style = MaterialTheme.typography.titleSmall)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(messages) {
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                ) {
                    Text(text = it.text, style = MaterialTheme.typography.bodyLarge)

                    Row {
                        Text("От: ${it.sender}", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.weight(1F))
                        if (it.synced) Text("✓", color = MaterialTheme.colorScheme.primary)
                    }

                    Text(
                        text = DateFormat.format("HH:mm:ss", it.timestamp).toString(),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}
