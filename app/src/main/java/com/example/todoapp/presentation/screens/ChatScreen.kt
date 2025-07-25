package com.example.todoapp.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todoapp.domain.model.ChatMessage
import com.example.todoapp.presentation.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    var messageToEdit by remember { mutableStateOf<ChatMessage?>(null) }
    var editText by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true,
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { message ->
                MessageBubble(
                    message = message,
                    onEditClick = {
                        messageToEdit = it
                        editText = it.text
                    },
                    onDeleteClick = {
                        viewModel.deleteMessage(it.id)
                    }
                )
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 74.dp)
        ) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter message") },
                maxLines = 4
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(messageText)
                        messageText = ""
                    }
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("Send")
            }
        }
    }

    if (messageToEdit != null) {
        AlertDialog(
            onDismissRequest = { messageToEdit = null },
            title = { Text("Edit Message") },
            text = {
                TextField(
                    value = editText,
                    onValueChange = { editText = it },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editText.isNotBlank() && messageToEdit != null) {
                            viewModel.editMessage(messageToEdit?.id.orEmpty(), editText)
                        }
                        messageToEdit = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { messageToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: ChatMessage,
    onEditClick: (ChatMessage) -> Unit,
    onDeleteClick: (ChatMessage) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isOwnMessage = message.senderId.startsWith("user")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = { expanded = true }
            ),
        horizontalAlignment = if (isOwnMessage) Alignment.End else Alignment.Start
    ) {
        Text(
            text = message.senderName,
            style = MaterialTheme.typography.labelSmall,
            color = if (isOwnMessage) Color(0xFF436171) else Color(0xFF9E9E9E)
        )

        Box(
            modifier = Modifier
                .background(
                    color = if (isOwnMessage) Color(0xFF436171) else Color(0xFF3D3F40),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 2.dp),
            color = if (isOwnMessage) Color(0xFF436171) else Color(0xFF9E9E9E)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    expanded = false
                    onEditClick(message)
                }
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    expanded = false
                    onDeleteClick(message)
                }
            )
        }
    }
}