package com.example.todoapp.data.repository

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import com.example.todoapp.domain.model.MediaItem
import com.example.todoapp.domain.repository.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.File

class PlayerRepositoryImpl(
    private val context: Context,
) : PlayerRepository {
    private val recentMediaFlow = MutableStateFlow<List<MediaItem>>(emptyList())

    override suspend fun getMediaItems() = getLocalMediaItems()

    override suspend fun getMediaItemById(id: String) = getLocalMediaItems().find { it.id == id }

    override suspend fun searchMediaItems(query: String) =
        getLocalMediaItems().filter {
            it.title.contains(query, true) ||
                it.artist?.contains(query, true) == true ||
                it.album?.contains(query, true) == true
        }

    override suspend fun saveMediaItem(mediaItem: MediaItem) {
        recentMediaFlow.update { items ->
            listOf(mediaItem) + items.filter { it.id != mediaItem.id }
        }
    }

    override suspend fun deleteMediaItem(id: String) {
        getMediaItemById(id)?.let {
            if (it.uri.startsWith("file://")) {
                withContext(Dispatchers.IO) {
                    val file =
                        File(
                            it.uri
                                .toUri()
                                .path
                                .orEmpty(),
                        )
                    if (file.exists()) file.delete()
                }
            }
        }
    }

    override suspend fun getLocalMediaItems(): List<MediaItem> =
        withContext(Dispatchers.IO) {
            val items = mutableListOf<MediaItem>()

            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                } else {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

            val projection =
                arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.ALBUM_ID,
                )

            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            context.contentResolver
                .query(
                    collection,
                    projection,
                    null,
                    null,
                    sortOrder,
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                    val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                    val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                    val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val title = cursor.getString(titleColumn) ?: "Unknown"
                        val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                        val album = cursor.getString(albumColumn) ?: "Unknown Album"
                        val duration = cursor.getLong(durationColumn)
                        val path = cursor.getString(dataColumn)
                        val displayName = cursor.getString(displayNameColumn) ?: title

                        val albumArtUri =
                            if (albumColumn >= 0) {
                                val albumId = cursor.getLong(albumIdColumn)
                                val albumArtUri = "content://media/external/audio/albumart".toUri()
                                ContentUris.withAppendedId(albumArtUri, albumId).toString()
                            } else {
                                null
                            }

                        val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                        val mediaItem =
                            MediaItem(
                                id = id.toString(),
                                title = title.ifEmpty { displayName },
                                artist = artist,
                                album = album,
                                duration = duration,
                                uri = contentUri.toString(),
                                artworkUri = albumArtUri,
                            )

                        items.add(mediaItem)
                    }
                }

            items
        }

    override suspend fun getNetworkMediaItems(): List<MediaItem> = emptyList()

    override fun observeRecentMedia() = recentMediaFlow
}
