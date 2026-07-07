package com.example.todoapp.data.remote

data class CallState(
    var callStartNanos: Long = 0,
    var dnsDuration: Long? = null,
    var connectDuration: Long? = null,
    var tlsDuration: Long? = null,
    var requestHeadersDuration: Long? = null,
    var requestBodyDuration: Long? = null,
    var responseHeadersDuration: Long? = null,
    var responseBodyDuration: Long? = null,
    var totalDuration: Long = 0,
    var responseCode: Int? = null,
    var errorMessage: String? = null,
    var success: Boolean = false,
    var url: String = "",
    var method: String = "",
    var startTimeMs: Long = 0,
)
