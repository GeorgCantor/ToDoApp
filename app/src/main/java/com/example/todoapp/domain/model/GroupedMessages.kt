package com.example.todoapp.domain.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.DAY_OF_YEAR
import java.util.Calendar.YEAR
import java.util.Calendar.getInstance
import java.util.Date
import java.util.Locale

data class MessageGroup(
    val header: String,
    val timestamp: Long,
    val messages: List<ChatMessage>,
)

enum class DateGroupType {
    TODAY,
    YESTERDAY,
    THIS_WEEK,
    THIS_MONTH,
    OLDER,
}

object MessageGrouper {
    fun groupMessages(messages: List<ChatMessage>): List<MessageGroup> {
        if (messages.isEmpty()) return emptyList()
        return messages
            .groupBy { getDateHeader(it.timestamp) }
            .map { (header, messages) ->
                MessageGroup(
                    header = header,
                    timestamp = messages.first().timestamp,
                    messages = messages,
                )
            }.sortedByDescending { it.timestamp }
    }

    private fun getDateHeader(timestamp: Long): String {
        val messageDate = Date(timestamp)
        val calendar = getInstance().apply { time = messageDate }
        val today = getInstance()
        return when {
            isSameDay(calendar, today) -> "Сегодня"
            isYesterday(calendar) -> "Вчера"
            isThisYear(calendar, today) -> {
                SimpleDateFormat("EEEE", Locale.getDefault()).format(messageDate)
            }
            isThisYear(calendar, today) -> {
                SimpleDateFormat("d MMMM", Locale.getDefault()).format(messageDate)
            }
            else -> SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(messageDate)
        }
    }

    private fun isSameDay(
        cal1: Calendar,
        cal2: Calendar,
    ) = isThisYear(cal1, cal2) && cal1.get(DAY_OF_YEAR) == cal2.get(DAY_OF_YEAR)

    private fun isYesterday(calendar: Calendar): Boolean {
        val yesterday = getInstance().apply { add(DAY_OF_YEAR, -1) }
        return isSameDay(calendar, yesterday)
    }

    private fun isThisWeek(
        cal1: Calendar,
        cal2: Calendar,
    ): Boolean {
        val weekAgo = getInstance().apply { add(DAY_OF_YEAR, -7) }
        return cal1.after(weekAgo) && !isSameDay(cal1, cal2) && !isYesterday(cal1)
    }

    private fun isThisYear(
        cal1: Calendar,
        cal2: Calendar,
    ) = cal1.get(YEAR) == cal2.get(YEAR)
}
