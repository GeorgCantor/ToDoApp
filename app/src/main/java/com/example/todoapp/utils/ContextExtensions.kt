package com.example.todoapp.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.widget.Toast

fun Context.isDebugBuild(): Boolean = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

fun Context.showToast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT,
) {
    Toast.makeText(this, message, duration).show()
}
