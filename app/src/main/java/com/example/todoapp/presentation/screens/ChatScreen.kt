package com.example.todoapp.presentation.screens

import android.media.MediaPlayer
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.todoapp.domain.model.ChatMessage
import com.example.todoapp.presentation.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val context = LocalContext.current
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    var messageToEdit by remember { mutableStateOf<ChatMessage?>(null) }
    var editText by remember { mutableStateOf("") }
    val isRecording by viewModel.isRecording.collectAsState()
    val recordingTime by viewModel.recordingTime.collectAsState()
    var currentPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true,
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { message ->
                MessageBubble(
                    message = message,
                    onEditClick = { messageToEdit = it },
                    onDeleteClick = { viewModel.deleteMessage(it.id) },
                    onPlayAudio = { base64 ->
                        currentPlayer?.release()
                        try {
                            val audioFile =
                                viewModel.repository.base64ToAudioFile(base64, context.cacheDir)
                            MediaPlayer().apply {
                                setDataSource(audioFile.absolutePath)
                                prepare()
                                start()
                                setOnCompletionListener {
                                    release()
                                    audioFile.delete()
                                    currentPlayer = null
                                }
                                currentPlayer = this
                            }
                        } catch (e: Exception) {
                        }
                    },
                )
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 74.dp)
        ) {
            if (isRecording) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            MaterialTheme.colorScheme.errorContainer,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Recording",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${recordingTime}s",
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { viewModel.stopRecording() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Stop")
                    }
                }
            } else {
                IconButton(
                    onClick = { viewModel.startRecording(context.cacheDir) },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Record voice",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

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
    onDeleteClick: (ChatMessage) -> Unit,
    onPlayAudio: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val isOwnMessage = message.senderId == "1"
    val bubbleColor = if (isOwnMessage) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.secondaryContainer
    val textColor = if (isOwnMessage) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSecondaryContainer

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
            color = textColor.copy(alpha = 0.7f)
        )

        when {
            message.audioBase64 != null -> {
                AudioMessageItem(
                    durationMs = message.durationMs ?: 0,
                    onPlayClick = { onPlayAudio(message.audioBase64) },
                    bubbleColor = bubbleColor,
                    textColor = textColor
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .background(bubbleColor, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .widthIn(max = 280.dp)
                ) {
                    Text(
                        text = message.text,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Text(
            text = SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(Date(message.timestamp)),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 2.dp),
            color = textColor.copy(alpha = 0.6f)
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

@Composable
fun AudioMessageItem(
    durationMs: Long,
    onPlayClick: () -> Unit,
    bubbleColor: Color,
    textColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(bubbleColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        IconButton(
            onClick = onPlayClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play audio",
                tint = textColor
            )
        }

        Text(
            text = "${durationMs / 1000}s",
            color = textColor,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}