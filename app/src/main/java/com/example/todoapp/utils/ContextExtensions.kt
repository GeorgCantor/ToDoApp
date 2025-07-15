package com.example.todoapp.utils

import android.content.Context
import android.content.pm.ApplicationInfo

fun Context.isDebugBuild(): Boolean {
    return (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
}