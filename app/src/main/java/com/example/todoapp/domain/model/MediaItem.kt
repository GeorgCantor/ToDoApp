package com.example.todoapp.domain.model

import androidx.core.net.toUri
import androidx.media3.common.MediaMetadata
import java.util.UUID

data class MediaItem(
    val id: String,
    val title: String,
    val artist: String? = null,
    val album: String? = null,
    val duration: Long = 0L,
    val uri: String,
    val artworkUri: String? = null,
    val mediaType: MediaType = MediaType.AUDIO,
    val metadata: Map<String, String> = emptyMap(),
) {
    enum class MediaType { AUDIO, VIDEO }

    companion object {
        fun fromUri(
            uri: String,
            title: String = "",
        ) = MediaItem(
            id = UUID.randomUUID().toString(),
            title = title.ifEmpty { uri.substringAfterLast('/') },
            uri = uri,
            mediaType = if (".mp4" in uri) MediaType.VIDEO else MediaType.AUDIO,
        )

        fun MediaItem.toExoMediaItem() =
            androidx.media3.common.MediaItem
                .fromUri(this.uri)
                .buildUpon()
                .setMediaMetadata(
                    MediaMetadata
                        .Builder()
                        .setTitle(this.title)
                        .setArtist(this.artist)
                        .setAlbumTitle(this.album)
                        .setArtworkUri(this.artworkUri?.toUri())
                        .build(),
                ).build()
    }
}
