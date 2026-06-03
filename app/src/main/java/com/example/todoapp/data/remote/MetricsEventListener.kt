package com.example.todoapp.data.remote

import com.example.todoapp.domain.model.NetworkMetrics
import com.example.todoapp.domain.repository.NetworkMetricsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.Handshake
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.UUID

class MetricsEventListener(
    private val repository: NetworkMetricsRepository,
    private val externalScope: CoroutineScope,
) : EventListener() {
    private val callStates = mutableMapOf<Call, CallState>()

    override fun callStart(call: Call) {
        callStates[call] =
            CallState().apply {
                callStartNanos = System.nanoTime()
                startTimeMs = System.currentTimeMillis()
                url = call.request().url.toString()
                method = call.request().method
            }
    }

    override fun dnsEnd(
        call: Call,
        domainName: String,
        inetAddressList: List<InetAddress>,
    ) {
        callStates[call]?.let {
            it.dnsDuration = (System.nanoTime() - it.callStartNanos) / 1_000_000
        }
    }

    override fun connectEnd(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?,
    ) {
        callStates[call]?.let {
            it.connectDuration = (System.nanoTime() - it.callStartNanos) / 1_000_000 - (it.dnsDuration ?: 0)
        }
    }

    override fun secureConnectEnd(
        call: Call,
        handshake: Handshake?,
    ) {
        callStates[call]?.let {
            it.tlsDuration = (System.nanoTime() - it.callStartNanos) / 1_000_000
        }
    }

    override fun requestHeadersEnd(
        call: Call,
        request: Request,
    ) {
        callStates[call]?.let {
            it.requestHeadersDuration = (System.nanoTime() - it.callStartNanos) / 1_000_000
        }
    }

    override fun requestBodyEnd(
        call: Call,
        byteCount: Long,
    ) {
        callStates[call]?.let {
            it.requestBodyDuration = (System.nanoTime() - it.callStartNanos) / 1_000_000
        }
    }

    override fun responseHeadersEnd(
        call: Call,
        response: Response,
    ) {
        callStates[call]?.let {
            it.responseHeadersDuration = (System.nanoTime() - it.callStartNanos) / 1_000_000
            it.responseCode = response.code
        }
    }

    override fun responseBodyEnd(
        call: Call,
        byteCount: Long,
    ) {
        callStates[call]?.let {
            it.responseBodyDuration = (System.nanoTime() - it.callStartNanos) / 1_000_000
            it.totalDuration = it.responseBodyDuration ?: ((System.nanoTime() - it.callStartNanos) / 1_000_000)
            it.success = it.errorMessage == null && (it.responseCode in 200..299)
            saveMetric(it, call)
        }
    }

    override fun callFailed(
        call: Call,
        ioe: IOException,
    ) {
        callStates[call]?.let {
            it.errorMessage = ioe.message
            it.totalDuration = (System.nanoTime() - it.callStartNanos) / 1_000_000
            it.success = false
            saveMetric(it, call)
        }
    }

    private fun saveMetric(
        state: CallState,
        call: Call,
    ) {
        val metrics =
            NetworkMetrics(
                id = UUID.randomUUID().toString(),
                url = state.url,
                method = state.method,
                startTimeMs = state.startTimeMs,
                dnsDurationMs = state.dnsDuration,
                connectDurationMs = state.connectDuration,
                tlsDurationMs = state.tlsDuration,
                requestHeadersDurationMs = state.requestHeadersDuration,
                requestBodyDurationMs = state.requestBodyDuration,
                responseHeadersDurationMs = state.responseHeadersDuration,
                responseBodyDurationMs = state.responseBodyDuration,
                totalDurationMs = state.totalDuration,
                responseCode = state.responseCode,
                errorMessage = state.errorMessage,
                success = state.success,
            )
        externalScope.launch {
            repository.saveMetrics(metrics)
        }
        callStates.remove(call)
    }

    fun cleanup() {
        externalScope.coroutineContext.cancelChildren()
    }
}
