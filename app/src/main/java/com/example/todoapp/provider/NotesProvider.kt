package com.example.todoapp.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import java.util.Date

class NotesProvider : ContentProvider() {
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    private val NOTES = 1
    private val NOTE_ID = 2

    private val notes = mutableListOf<Note>()

    init {
        uriMatcher.addURI(NotesContract.AUTHORITY, NotesContract.Notes.TABLE_NAME, NOTES)
        uriMatcher.addURI(NotesContract.AUTHORITY, "${NotesContract.Notes.TABLE_NAME}/#", NOTE_ID)

        notes.addAll(
            listOf(
                Note("Покупки", "Купить молоко, хлеб, яйца", false),
                Note("Учеба", "Изучить Content Provider", false),
                Note("Работа", "Закончить проект", true),
            ),
        )
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int =
        when (uriMatcher.match(uri)) {
            NOTE_ID -> {
                val id = uri.lastPathSegment?.toLongOrNull() ?: -1
                if (id in 0 until notes.size) {
                    notes.removeAt(id.toInt())
                    context?.contentResolver?.notifyChange(uri, null)
                    1
                } else {
                    0
                }
            }

            else -> throw IllegalArgumentException("Delete not supported for: $uri")
        }

    override fun getType(uri: Uri): String =
        when (uriMatcher.match(uri)) {
            NOTES -> NotesContract.Notes.CONTENT_TYPE
            NOTE_ID -> NotesContract.Notes.CONTENT_ITEM_TYPE
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

    override fun insert(
        uri: Uri,
        values: ContentValues?,
    ): Uri {
        when (uriMatcher.match(uri)) {
            NOTES -> {
                val note =
                    Note(
                        title = values?.getAsString(NotesContract.Notes.COLUMN_TITLE).orEmpty(),
                        content = values?.getAsString(NotesContract.Notes.COLUMN_CONTENT).orEmpty(),
                        isCompleted =
                            values?.getAsBoolean(NotesContract.Notes.COLUMN_IS_COMPLETED)
                                ?: false,
                    )
                notes.add(note)
                val newId = (notes.size - 1).toLong()
                context?.contentResolver?.notifyChange(uri, null)
                return Uri.withAppendedPath(NotesContract.Notes.CONTENT_URI, newId.toString())
            }

            else -> throw IllegalArgumentException("Insert not supported for: $uri")
        }
    }

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor =
        when (uriMatcher.match(uri)) {
            NOTES -> {
                val cursor =
                    MatrixCursor(
                        projection ?: arrayOf(
                            NotesContract.Notes._ID,
                            NotesContract.Notes.COLUMN_TITLE,
                            NotesContract.Notes.COLUMN_CONTENT,
                            NotesContract.Notes.COLUMN_IS_COMPLETED,
                            NotesContract.Notes.COLUMN_CREATED_AT,
                            NotesContract.Notes.COLUMN_UPDATED_AT,
                        ),
                    )
                notes.forEachIndexed { index, note ->
                    cursor.addRow(
                        arrayOf(
                            index.toLong(),
                            note.title,
                            note.content,
                            if (note.isCompleted) 1 else 0,
                            note.createdAt.time,
                            note.updatedAt.time,
                        ),
                    )
                }
                cursor
            }

            NOTE_ID -> {
                val id = uri.lastPathSegment?.toLongOrNull() ?: -1
                val note =
                    notes.getOrNull(id.toInt()) ?: throw IllegalArgumentException("Note not found")

                val cursor =
                    MatrixCursor(
                        arrayOf(
                            NotesContract.Notes._ID,
                            NotesContract.Notes.COLUMN_TITLE,
                            NotesContract.Notes.COLUMN_CONTENT,
                            NotesContract.Notes.COLUMN_IS_COMPLETED,
                            NotesContract.Notes.COLUMN_CREATED_AT,
                            NotesContract.Notes.COLUMN_UPDATED_AT,
                        ),
                    )

                cursor.addRow(
                    arrayOf(
                        id,
                        note.title,
                        note.content,
                        if (note.isCompleted) 1 else 0,
                        note.createdAt.time,
                        note.updatedAt.time,
                    ),
                )
                cursor
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        return when (uriMatcher.match(uri)) {
            NOTE_ID -> {
                val id = uri.lastPathSegment?.toLongOrNull() ?: -1
                val note = notes.getOrNull(id.toInt()) ?: return 0

                values?.keySet()?.forEach { key ->
                    when (key) {
                        NotesContract.Notes.COLUMN_TITLE ->
                            note.title = values.getAsString(key) ?: note.title

                        NotesContract.Notes.COLUMN_CONTENT ->
                            note.content = values.getAsString(key) ?: note.content

                        NotesContract.Notes.COLUMN_IS_COMPLETED ->
                            note.isCompleted = values.getAsBoolean(key) ?: note.isCompleted
                    }
                }
                note.updatedAt = Date()

                context?.contentResolver?.notifyChange(uri, null)
                1
            }

            else -> throw IllegalArgumentException("Update not supported for: $uri")
        }
    }

    data class Note(
        var title: String,
        var content: String,
        var isCompleted: Boolean,
        val createdAt: Date = Date(),
        var updatedAt: Date = Date(),
    )
}
