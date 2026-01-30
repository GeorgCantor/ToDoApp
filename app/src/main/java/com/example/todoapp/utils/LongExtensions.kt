package com.example.todoapp.utils

import java.util.concurrent.TimeUnit

fun Long.formatTime(): String {
    if (this <= 0) return "0:00"
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(minutes)

    return String.format("%d:%02d", minutes, seconds)
}
