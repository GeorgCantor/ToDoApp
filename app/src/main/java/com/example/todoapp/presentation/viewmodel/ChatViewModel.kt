package com.example.todoapp.presentation.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.repository.ChatRepositoryImpl
import com.example.todoapp.domain.model.ChatMessage
import com.example.todoapp.domain.usecase.GetChatMessagesUseCase
import com.example.todoapp.domain.usecase.SendMessageUseCase
import com.example.todoapp.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class ChatViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getMessagesUseCase: GetChatMessagesUseCase
) : ViewModel() {
    val repository = ChatRepositoryImpl()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> get() = _messages

    private var _permissionGranted = mutableStateOf(false)
    val permissionGranted: State<Boolean> = _permissionGranted

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> get() = _isRecording

    private val _recordingTime = MutableStateFlow(0L)
    val recordingTime: StateFlow<Long> get() = _recordingTime

    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null

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

    fun editMessage(key: String, newText: String) {
        viewModelScope.launch {
            try {
                repository.editMessage(key, newText)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteMessage(key: String) {
        viewModelScope.launch {
            try {
                repository.deleteMessage(key)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startRecording(context: Context, cacheDir: File) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _permissionGranted.value = false
            return
        }

        _isRecording.value = true
        _recordingTime.value = 0L

        try {
            audioFile = File.createTempFile("audio", ".mp3", cacheDir)
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile?.absolutePath)
                prepare()
                start()
            }

            viewModelScope.launch {
                while (_isRecording.value) {
                    kotlinx.coroutines.delay(1000)
                    _recordingTime.value += 1
                }
            }
        } catch (e: Exception) {
            _isRecording.value = false
            context.showToast(e.message.orEmpty())
        }
    }

    fun stopRecording() {
        _isRecording.value = false
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaRecorder = null
        }

        audioFile?.let { file ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val (base64, duration) = repository.audioToBase64(file)
                    sendAudioMessage(base64, duration)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    file.delete()
                }
            }
        }
    }

    private suspend fun sendAudioMessage(audioBase64: String, durationMs: Long) {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = "",
            senderId = "1",
            senderName = "Alex",
            timestamp = System.currentTimeMillis(),
            audioBase64 = audioBase64,
            durationMs = durationMs
        )
        sendMessageUseCase(message)
    }
}
