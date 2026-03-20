package com.example.todoapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Message(
    val id: String,
    val text: String,
    val sender: String,
    val timestamp: Long,
    val synced: Boolean = false,
) : Parcelable
