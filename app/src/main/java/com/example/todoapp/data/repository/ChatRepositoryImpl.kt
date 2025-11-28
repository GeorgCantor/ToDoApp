package com.example.todoapp.data.repository

import android.media.MediaPlayer
import android.util.Base64
import com.example.todoapp.domain.model.ChatMessage
import com.example.todoapp.domain.repository.ChatRepository
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ChatRepositoryImpl : ChatRepository {
    private val database = Firebase.database.reference.child("chat_messages")

    override suspend fun sendMessage(message: ChatMessage) {
        database.push().setValue(message).await()
    }

    override suspend fun getMessages(): List<ChatMessage> =
        try {
            val snapshot = database.get().await()
            snapshot.children.mapNotNull { child ->
                child.getValue(ChatMessage::class.java)?.copy(id = child.key.orEmpty())
            }
        } catch (e: Exception) {
            emptyList()
        }

    override fun observeMessages(): Flow<List<ChatMessage>> =
        callbackFlow {
            val listener =
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val messages =
                            snapshot.children.mapNotNull { child ->
                                child.getValue(ChatMessage::class.java)?.copy(id = child.key.orEmpty())
                            }
                        trySend(messages).isSuccess
                    }

                    override fun onCancelled(error: DatabaseError) {
                        close(error.toException())
                    }
                }

            database.addValueEventListener(listener)
            awaitClose { database.removeEventListener(listener) }
        }

    override suspend fun editMessage(
        messageId: String,
        newText: String,
    ) {
        database
            .child(messageId)
            .child("text")
            .setValue(newText)
            .await()
    }

    override suspend fun deleteMessage(messageId: String) {
        database.child(messageId).removeValue().await()
    }

    override suspend fun audioToBase64(file: File): Pair<String, Long> {
        val bytes = file.readBytes()
        val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
        val duration = getAudioDuration(file)
        return Pair(base64, duration)
    }

    override fun base64ToAudioFile(
        base64: String,
        cacheDir: File,
    ): File {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        val tempFile = File.createTempFile("audio", ".mp3", cacheDir)
        FileOutputStream(tempFile).use { it.write(bytes) }
        return tempFile
    }

    private suspend fun getAudioDuration(file: File): Long =
        withContext(Dispatchers.IO) {
            val mediaPlayer = MediaPlayer()
            try {
                mediaPlayer.setDataSource(file.absolutePath)
                return@withContext suspendCancellableCoroutine { continuation ->
                    mediaPlayer.setOnPreparedListener {
                        continuation.resume(mediaPlayer.duration.toLong())
                    }
                    mediaPlayer.setOnErrorListener { _, what, extra ->
                        continuation.resumeWithException(IOException("MediaPlayer error: $what, $extra"))
                        true
                    }
                    mediaPlayer.prepareAsync()
                    continuation.invokeOnCancellation { mediaPlayer.release() }
                }
            } catch (e: Exception) {
                mediaPlayer.release()
                throw e
            } finally {
                mediaPlayer.release()
            }
        }
}
