package com.example.todoapp.domain.model

data class Playlist(
    val id: String,
    val name: String,
    val items: List<MediaItem>,
    val isShuffled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.NONE,
) {
    enum class RepeatMode { NONE, ONE, ALL }

    fun getNextItem(currentId: String): MediaItem? {
        val currentIndex = items.indexOfFirst { it.id == currentId }
        if (currentIndex == -1) return null
        return when {
            isShuffled -> items.random()
            currentIndex < items.lastIndex -> items[currentIndex + 1]
            repeatMode == RepeatMode.ALL -> items.first()
            else -> null
        }
    }
}
