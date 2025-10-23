package com.example.todoapp.domain.model

import android.os.Bundle

data class WidgetIntentData(
    val type: WidgetIntentType,
    val targetScreen: String,
    val extraData: Bundle = Bundle(),
)

enum class WidgetIntentType {
    NEW_MESSAGE,
    OPEN_CHAT,
}
