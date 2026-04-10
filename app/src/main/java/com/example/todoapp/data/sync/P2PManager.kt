package com.example.todoapp.data.sync

import android.content.Context
import com.example.todoapp.domain.crypto.EncryptionManager
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.json.Json
import java.security.KeyPair
import java.security.PublicKey
import java.util.UUID
import javax.crypto.SecretKey

enum class ConnectionStatus {
    IDLE,
    ADVERTISING,
    DISCOVERING,
    CONNECTED,
}

class P2PManager(
    context: Context,
) {
    private val connectionsClient = Nearby.getConnectionsClient(context)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.IDLE)
    val connectionStatus = _connectionStatus.asStateFlow()

    private val _incomingMessages = Channel<Message>(Channel.BUFFERED)
    val incomingMessages: Flow<Message> = _incomingMessages.receiveAsFlow()

    private val serviceId = "com.example.todoapp.P2P_SYNC"
    private val localEndpointName = android.os.Build.MODEL
    private var currentEndpointId: String? = null
    private var isAdvertising = false
    private var isDiscovering = false

    private var keyPair: KeyPair? = null
    private var sessionKey: SecretKey? = null
    private var remotePublicKey: PublicKey? = null
    private var isKeyExchangeComplete = false

    init {
        keyPair = EncryptionManager.generateKeyPair()
    }

    private val connectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(
                endpointId: String,
                info: ConnectionInfo,
            ) {
                keyPair?.let {
                    val publicKey = EncryptionManager.publicKeyToString(it.public)
                    val keyPayload = Payload.fromBytes(publicKey.toByteArray())
                    connectionsClient.sendPayload(endpointId, keyPayload)
                }
                connectionsClient.acceptConnection(endpointId, payloadCallback)
                _connectionStatus.value = ConnectionStatus.CONNECTED
            }

            override fun onConnectionResult(
                endpointId: String,
                resolution: ConnectionResolution,
            ) {
                if (resolution.status.isSuccess) {
                    currentEndpointId = endpointId
                }
            }

            override fun onDisconnected(endpointId: String) {
                if (endpointId == currentEndpointId) {
                    currentEndpointId = null
                    sessionKey = null
                    remotePublicKey = null
                    isKeyExchangeComplete = false
                    _connectionStatus.value = ConnectionStatus.IDLE
                }
            }
        }

    private val payloadCallback =
        object : PayloadCallback() {
            override fun onPayloadReceived(
                endpointId: String,
                payload: Payload,
            ) {
                if (payload.type == Payload.Type.BYTES) {
                    payload.asBytes()?.let { data ->
                        val dataString = String(data)
                        when {
                            !isKeyExchangeComplete && remotePublicKey == null -> {
                                try {
                                    remotePublicKey = EncryptionManager.stringToPublicKey(dataString)
                                    keyPair?.let {
                                        val sharedSecret = EncryptionManager.computeSharedSecret(it.private, remotePublicKey!!)
                                        sessionKey = EncryptionManager.deriveAesKey(sharedSecret)
                                        isKeyExchangeComplete = true
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                sendMessage(
                                    Message(
                                        id = UUID.randomUUID().toString(),
                                        text = "SYNC_REQUEST",
                                        sender = localEndpointName,
                                        timestamp = System.currentTimeMillis(),
                                        synced = false,
                                        isEncrypted = true,
                                    ),
                                )
                            }
                            else -> {
                                try {
                                    sessionKey?.let { EncryptionManager.decrypt(dataString, it) }?.let {
                                        _incomingMessages.trySend(Json.decodeFromString<Message>(it))
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
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
        _connectionStatus.value = ConnectionStatus.ADVERTISING
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
                _connectionStatus.value = ConnectionStatus.IDLE
                onFailure(it)
            }
    }

    fun startDiscovery(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        if (isDiscovering) return
        _connectionStatus.value = ConnectionStatus.DISCOVERING
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
                _connectionStatus.value = ConnectionStatus.IDLE
                onFailure(it)
            }
    }

    fun sendMessage(message: Message): Boolean {
        currentEndpointId ?: return false
        if (!isKeyExchangeComplete) return false

        val json = Json.encodeToString(Message.serializer(), message)
        val payloadData =
            if (sessionKey != null) {
                EncryptionManager.encrypt(json, sessionKey!!)
            } else {
                json
            }
        val payload = Payload.fromBytes(payloadData.toByteArray())
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
        sessionKey = null
        remotePublicKey = null
        isKeyExchangeComplete = false
        _connectionStatus.value = ConnectionStatus.IDLE
        scope.cancel()
    }
}
