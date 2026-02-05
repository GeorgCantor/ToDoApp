package com.example.todoapp.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.example.todoapp.domain.model.MediaItem
import com.example.todoapp.domain.model.PlaybackState
import com.example.todoapp.domain.model.PlayerState
import com.example.todoapp.domain.model.PlayerUiState
import com.example.todoapp.domain.usecase.DeleteMediaItemUseCase
import com.example.todoapp.domain.usecase.GetLocalMediaUseCase
import com.example.todoapp.domain.usecase.GetMediaItemsUseCase
import com.example.todoapp.domain.usecase.GetRecentMediaUseCase
import com.example.todoapp.domain.usecase.ManagePlayerUseCase
import com.example.todoapp.domain.usecase.PlayMediaUseCase
import com.example.todoapp.domain.usecase.SaveMediaItemUseCase
import com.example.todoapp.service.PlayerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@UnstableApi
class PlayerViewModel(
    application: Application,
    private val managePlayerUseCase: ManagePlayerUseCase,
    private val playMediaUseCase: PlayMediaUseCase,
    private val getMediaItemsUseCase: GetMediaItemsUseCase,
    private val getRecentMediaUseCase: GetRecentMediaUseCase,
    private val saveMediaItemUseCase: SaveMediaItemUseCase,
    private val deleteMediaItemUseCase: DeleteMediaItemUseCase,
    private val getLocalMediaUseCase: GetLocalMediaUseCase,
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    val playerState: StateFlow<PlayerState> = managePlayerUseCase.playerState
    val playbackState: StateFlow<PlaybackState> = managePlayerUseCase.playbackState

    private val _mediaItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val mediaItems: StateFlow<List<MediaItem>> = _mediaItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var isServiceRunning = false

    init {
        loadMediaItems()
        observePlayerState()
        observeRecentMedia()
        startPlayerService()
    }

    private fun loadMediaItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _mediaItems.value = getMediaItemsUseCase()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            combine(
                managePlayerUseCase.playerState,
                managePlayerUseCase.playbackState,
            ) { playerState, playbackState ->
                PlayerUiState(
                    currentMediaItem = playerState.currentMediaItem,
                    isPlaying = playerState.isPlaying,
                    currentPosition = playerState.currentPosition,
                    duration = playerState.duration,
                    bufferedPercentage = playerState.bufferedPercentage,
                    playbackState = playbackState,
                    error = playerState.error?.message,
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    private fun observeRecentMedia() {
        viewModelScope.launch {
            getRecentMediaUseCase().collect {
                _uiState.value = _uiState.value.copy(recentMedia = it)
            }
        }
    }

    fun playMedia(mediaItem: MediaItem) {
        viewModelScope.launch {
            saveMediaItemUseCase(mediaItem)
        }
        playMediaUseCase(mediaItem)
    }

    fun playPlaylist(
        playlist: List<MediaItem>,
        startIndex: Int = 0,
    ) {
        viewModelScope.launch {
            playlist.forEach { saveMediaItemUseCase(it) }
            playMediaUseCase(playlist, startIndex)
        }
    }

    fun togglePlayPause() {
        managePlayerUseCase.playPause()
    }

    fun seekTo(position: Long) {
        managePlayerUseCase.seekTo(position)
    }

    fun next() {
        managePlayerUseCase.next()
    }

    fun previous() {
        managePlayerUseCase.previous()
    }

    fun search(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isNotBlank()) {
                _mediaItems.value = getMediaItemsUseCase(query)
            } else {
                loadMediaItems()
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        loadMediaItems()
    }

    fun deleteMediaItem(id: String) {
        viewModelScope.launch {
            try {
                deleteMediaItemUseCase(id)
                _mediaItems.value = _mediaItems.value.filter { it.id != id }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun loadLocalMedia() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _mediaItems.value = getLocalMediaUseCase()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun startPlayerService() {
        if (!isServiceRunning) {
            viewModelScope.launch {
                try {
                    PlayerService.startService(getApplication())
                    isServiceRunning = true
                    managePlayerUseCase.setupAudioFocus()
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            error = "Failed to start background service: ${e.message}",
                        )
                }
            }
        }
    }

    private fun stopPlayerService() {
        viewModelScope.launch {
            try {
                PlayerService.stopService(getApplication())
                isServiceRunning = false
                managePlayerUseCase.releaseAudioFocus()
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = "Failed to stop background service: ${e.message}",
                    )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            if (isServiceRunning) stopPlayerService()
        }
    }
}
