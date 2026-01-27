package com.example.todoapp.domain.repository

import com.example.todoapp.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    suspend fun getMediaItems(): List<MediaItem>

    suspend fun getMediaItemById(id: String): MediaItem?

    suspend fun searchMediaItems(query: String): List<MediaItem>

    suspend fun saveMediaItem(mediaItem: MediaItem)

    suspend fun deleteMediaItem(id: String)

    suspend fun getLocalMediaItems(): List<MediaItem>

    suspend fun getNetworkMediaItems(): List<MediaItem>

    fun observeRecentMedia(): Flow<List<MediaItem>>
}
