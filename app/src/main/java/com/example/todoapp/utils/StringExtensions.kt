package com.example.todoapp.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun String.toFormattedDate(): String {
    val dateFormats =
        listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
        )

    for (format in dateFormats) {
        try {
            val inputFormat = SimpleDateFormat(format, Locale.getDefault())
            val date = inputFormat.parse(this)
            if (date != null) {
                val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                return outputFormat.format(date)
            }
        } catch (e: Exception) {
            continue
        }
    }

    return this
}
