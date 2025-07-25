package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.repository.ChatRepositoryImpl
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
    private val repository = ChatRepositoryImpl()
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> get() = _messages

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            repository.observeMessages().collect { newMessages ->
                _messages.value = newMessages.sortedByDescending { it.timestamp }
            }
        }
    }

    fun sendMessage(text: String, senderId: String = "1", senderName: String = "Alex") {
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