package com.example.todoapp.provider

import android.net.Uri

object NotesContract {
    const val AUTHORITY = "com.example.todoapp.provider"
    val BASE_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY")

    object Notes {
        const val _ID = "_id"
        const val TABLE_NAME = "notes"
        val CONTENT_URI: Uri = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build()
        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.$AUTHORITY.$TABLE_NAME"
        const val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.$AUTHORITY.$TABLE_NAME"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_UPDATED_AT = "updated_at"
        const val COLUMN_IS_COMPLETED = "is_completed"
    }
}
