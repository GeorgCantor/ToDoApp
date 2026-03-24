package com.example.todoapp.data.sync

import android.content.Context
import com.example.todoapp.domain.model.Message
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.json.Json
import java.util.UUID

class P2PManager(
    context: Context,
) {
    private val connectionsClient = Nearby.getConnectionsClient(context)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _incomingMessages = Channel<Message>(Channel.BUFFERED)
    val incomingMessages: Flow<Message> = _incomingMessages.receiveAsFlow()

    private val serviceId = "com.example.todoapp.P2P_SYNC"
    private val localEndpointName = android.os.Build.MODEL
    private var currentEndpointId: String? = null
    private var isAdvertising = false
    private var isDiscovering = false

    private val connectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(
                endpointId: String,
                info: ConnectionInfo,
            ) {
                connectionsClient.acceptConnection(endpointId, payloadCallback)
            }

            override fun onConnectionResult(
                endpointId: String,
                resolution: ConnectionResolution,
            ) {
                if (resolution.status.isSuccess) {
                    currentEndpointId = endpointId
                    sendMessage(
                        Message(
                            id = UUID.randomUUID().toString(),
                            text = "SYNC_REQUEST",
                            sender = localEndpointName,
                            timestamp = System.currentTimeMillis(),
                            synced = false,
                        ),
                    )
                }
            }

            override fun onDisconnected(endpointId: String) {
                if (endpointId == currentEndpointId) currentEndpointId = null
            }
        }

    private val payloadCallback =
        object : PayloadCallback() {
            override fun onPayloadReceived(
                endpointId: String,
                payload: Payload,
            ) {
                if (payload.type == Payload.Type.BYTES) {
                    payload.asBytes()?.let {
                        try {
                            _incomingMessages.trySend(Json.decodeFromString<Message>(String(it)))
                        } catch (e: Exception) {
                        }
                    }
                }
            }

            override fun onPayloadTransferUpdate(
                endpointId: String,
                update: PayloadTransferUpdate,
            ) {}
        }

    fun startAdvertising(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        if (isAdvertising) return
        val options = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        connectionsClient
            .startAdvertising(
                localEndpointName,
                serviceId,
                connectionLifecycleCallback,
                options,
            ).addOnSuccessListener {
                isAdvertising = true
                onSuccess()
            }.addOnFailureListener {
                onFailure(it)
            }
    }

    fun startDiscovery(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        if (isDiscovering) return
        connectionsClient
            .startDiscovery(
                serviceId,
                object : EndpointDiscoveryCallback() {
                    override fun onEndpointFound(
                        endpointId: String,
                        info: DiscoveredEndpointInfo,
                    ) {
                        connectionsClient.requestConnection(localEndpointName, endpointId, connectionLifecycleCallback)
                    }

                    override fun onEndpointLost(endpointId: String) {
                    }
                },
                DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build(),
            ).addOnSuccessListener {
                isDiscovering = true
                onSuccess()
            }.addOnFailureListener {
                onFailure(it)
            }
    }

    fun sendMessage(message: Message): Boolean {
        currentEndpointId ?: return false
        val json = Json.encodeToString(Message.serializer(), message)
        val payload = Payload.fromBytes(json.toByteArray())
        connectionsClient.sendPayload(currentEndpointId!!, payload)
        return true
    }

    fun stop() {
        if (isAdvertising) {
            connectionsClient.stopAdvertising()
            isAdvertising = false
        }
        if (isDiscovering) {
            connectionsClient.stopDiscovery()
            isDiscovering = false
        }
        currentEndpointId?.let {
            connectionsClient.disconnectFromEndpoint(it)
            currentEndpointId = null
        }
        scope.cancel()
    }
}
