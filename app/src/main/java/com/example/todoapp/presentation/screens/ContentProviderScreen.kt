package com.example.todoapp.presentation.screens

import android.content.ContentValues
import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todoapp.provider.NotesContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val isCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentProviderScreen(navController: NavController) {
    val context = LocalContext.current
    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun loadNotes() {
        coroutineScope.launch {
            isLoading = true
            try {
                withContext(Dispatchers.IO) {
                    val cursor = context.contentResolver.query(
                        NotesContract.Notes.CONTENT_URI,
                        null, null, null, null
                    )
                    val loadedNotes = mutableListOf<Note>()
                    cursor?.use { c ->
                        while (c.moveToNext()) {
                            val note = Note(
                                id = c.getLong(c.getColumnIndexOrThrow(NotesContract.Notes._ID)),
                                title = c.getString(c.getColumnIndexOrThrow(NotesContract.Notes.COLUMN_TITLE)),
                                content = c.getString(c.getColumnIndexOrThrow(NotesContract.Notes.COLUMN_CONTENT)),
                                isCompleted = c.getInt(c.getColumnIndexOrThrow(NotesContract.Notes.COLUMN_IS_COMPLETED)) == 1,
                                createdAt = c.getLong(c.getColumnIndexOrThrow(NotesContract.Notes.COLUMN_CREATED_AT)),
                                updatedAt = c.getLong(c.getColumnIndexOrThrow(NotesContract.Notes.COLUMN_UPDATED_AT))
                            )
                            loadedNotes.add(note)
                        }
                    }
                    notes = loadedNotes
                    errorMessage = null
                }
            } catch (e: Exception) {
                errorMessage = "Error loading notes: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun addNote(title: String, content: String) {
        coroutineScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val values = ContentValues().apply {
                        put(NotesContract.Notes.COLUMN_TITLE, title)
                        put(NotesContract.Notes.COLUMN_CONTENT, content)
                        put(NotesContract.Notes.COLUMN_IS_COMPLETED, false)
                    }
                    context.contentResolver.insert(NotesContract.Notes.CONTENT_URI, values)
                }
                loadNotes()
                showAddDialog = false
            } catch (e: Exception) {
                errorMessage = "Error adding note: ${e.message}"
            }
        }
    }

    fun deleteNote(noteId: Long) {
        coroutineScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val uri =
                        Uri.withAppendedPath(NotesContract.Notes.CONTENT_URI, noteId.toString())
                    context.contentResolver.delete(uri, null, null)
                }
                loadNotes()
            } catch (e: Exception) {
                errorMessage = "Error deleting note: ${e.message}"
            }
        }
    }

    fun toggleNoteCompletion(noteId: Long, currentStatus: Boolean) {
        coroutineScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val values = ContentValues().apply {
                        put(NotesContract.Notes.COLUMN_IS_COMPLETED, !currentStatus)
                    }
                    val uri =
                        Uri.withAppendedPath(NotesContract.Notes.CONTENT_URI, noteId.toString())
                    context.contentResolver.update(uri, values, null, null)
                }
                loadNotes()
            } catch (e: Exception) {
                errorMessage = "Error updating note: ${e.message}"
            }
        }
    }

    LaunchedEffect(Unit) {
        loadNotes()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Content Provider Demo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Note")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notes Count: ${notes.size}")
                    Button(onClick = { loadNotes() }) {
                        Text("Refresh")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes) { note ->
                    NoteCard(
                        note = note,
                        onDelete = { deleteNote(note.id) },
                        onToggleComplete = { toggleNoteCompletion(note.id, note.isCompleted) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddNoteDialog(
                onAddNote = { title, content -> addNote(title, content) },
                onDismiss = { showAddDialog = false }
            )
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Row {
                    IconButton(onClick = onToggleComplete) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Toggle Complete",
                            tint = if (note.isCompleted) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Status: ${if (note.isCompleted) "Completed" else "Active"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AddNoteDialog(
    onAddNote: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Note") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onAddNote(title, content)
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}