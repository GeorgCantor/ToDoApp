package com.example.todoapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.example.todoapp.R
import com.example.todoapp.presentation.MainActivity

@UnstableApi
class PlayerService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private var notificationManager: PlayerNotificationManager? = null
    private var player: ExoPlayer? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        player =
            ExoPlayer.Builder(this).build().apply {
                mediaSession = MediaSession.Builder(this@PlayerService, this).build()
                setupNotification(this)
            }
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onGetSession(info: MediaSession.ControllerInfo) = mediaSession

    override fun onDestroy() {
        mediaSession?.let {
            player?.release()
            it.release()
            mediaSession = null
        }
        notificationManager?.setPlayer(null)
        notificationManager = null
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Music player",
                    NotificationManager.IMPORTANCE_LOW,
                ).apply {
                    description = "Music player is running"
                    setSound(null, null)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun setupNotification(player: ExoPlayer) {
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE,
            )
        notificationManager =
            PlayerNotificationManager
                .Builder(this, NOTIFICATION_ID, CHANNEL_ID)
                .apply {
                    setNotificationListener(
                        object : PlayerNotificationManager.NotificationListener {
                            override fun onNotificationPosted(
                                notificationId: Int,
                                notification: Notification,
                                ongoing: Boolean,
                            ) {
                                if (ongoing) {
                                    startForeground(notificationId, notification)
                                }
                            }

                            override fun onNotificationCancelled(
                                notificationId: Int,
                                dismissedByUser: Boolean,
                            ) {
                                stopSelf()
                            }
                        },
                    )
                }.build()
                .apply {
                    setPlayer(player)
                }
    }

    private fun createNotification() =
        NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setSmallIcon(R.drawable.widget_preview)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

    companion object {
        private const val CHANNEL_ID = "player_channel"
        private const val NOTIFICATION_ID = 1001

        fun startService(context: Context) {
            val intent = Intent(context, PlayerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, PlayerService::class.java)
            context.stopService(intent)
        }
    }
}
