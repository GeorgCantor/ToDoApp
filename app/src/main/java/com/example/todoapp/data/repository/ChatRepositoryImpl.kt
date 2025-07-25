package com.example.todoapp.data.repository

import com.example.todoapp.domain.model.ChatMessage
import com.example.todoapp.domain.repository.ChatRepository
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepositoryImpl : ChatRepository {
    private val database = Firebase.database.reference.child("chat_messages")

    override suspend fun sendMessage(message: ChatMessage) {
        database.push().setValue(message).await()
    }

    override suspend fun getMessages(): List<ChatMessage> {
        return try {
            val snapshot = database.get().await()
            snapshot.children.mapNotNull { child ->
                child.getValue(ChatMessage::class.java)?.copy(id = child.key.orEmpty())
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun observeMessages(): Flow<List<ChatMessage>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { child ->
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

    suspend fun editMessage(id: String, newText: String) {
        database.child(id).child("text").setValue(newText).await()
    }

    suspend fun deleteMessage(id: String) {
        database.child(id).removeValue().await()
    }
}
