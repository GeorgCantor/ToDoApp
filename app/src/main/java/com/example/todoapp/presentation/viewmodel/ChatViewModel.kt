package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.model.ChatMessage
import com.example.todoapp.domain.usecase.GetChatMessagesUseCase
import com.example.todoapp.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getMessagesUseCase: GetChatMessagesUseCase
) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> get() = _messages

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            try {
                _messages.value = getMessagesUseCase()
            } catch (e: Exception) {
            }
        }
    }

    fun sendMessage(text: String, senderId: String, senderName: String) {
        viewModelScope.launch {
            sendMessageUseCase(
                ChatMessage(
                    text = text,
                    senderId = senderId,
                    senderName = senderName
                )
            )
        }
    }
}