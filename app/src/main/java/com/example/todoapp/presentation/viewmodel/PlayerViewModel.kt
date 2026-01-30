package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val managePlayerUseCase: ManagePlayerUseCase,
    private val playMediaUseCase: PlayMediaUseCase,
    private val getMediaItemsUseCase: GetMediaItemsUseCase,
    private val getRecentMediaUseCase: GetRecentMediaUseCase,
    private val saveMediaItemUseCase: SaveMediaItemUseCase,
    private val deleteMediaItemUseCase: DeleteMediaItemUseCase,
    private val getLocalMediaUseCase: GetLocalMediaUseCase,
) : ViewModel() {
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

    init {
        loadMediaItems()
        observePlayerState()
        observeRecentMedia()
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
}
