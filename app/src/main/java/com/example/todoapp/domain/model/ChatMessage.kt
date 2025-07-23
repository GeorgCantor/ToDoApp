package com.example.todoapp.domain.model

data class ChatMessage(
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val senderName: String = "User",
    val timestamp: Long = System.currentTimeMillis()
)