package com.example.todoapp.data.objectinspector.core

import java.text.SimpleDateFormat
import java.util.Date

// Отвечает за преобразование значений объектов в читаемые строки
object ValueFormatter {
    /**
     * Преобразует любой объект в строковое представление.
     *
     * @param obj Объект для форматирования
     * @return Строковое представление объекта
     */
    fun format(obj: Any?): String =
        when (obj) {
            null -> "null"
            is String -> "\"$obj\""
            is Char -> "'$obj'"
            is Number -> obj.toString()
            is Boolean -> obj.toString()
            is Date -> {
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                "Date(${format.format(obj)})"
            }
            is Array<*> -> if (obj.isEmpty()) "[]" else "[${obj.size}]"
            is Collection<*> -> if (obj.isEmpty()) "[]" else "${obj.javaClass.simpleName}[${obj.size}]"
            is Map<*, *> -> if (obj.isEmpty()) "{}" else "Map[${obj.size}]"
            else -> {
                val str = obj.toString()
                if (str.startsWith("${obj.javaClass.simpleName}@")) {
                    // Это стандартная реализация toString() - объект не переопределил его
                    obj.javaClass.simpleName
                } else {
                    str
                }
            }
        }
}
