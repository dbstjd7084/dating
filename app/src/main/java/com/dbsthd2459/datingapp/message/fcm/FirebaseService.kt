package com.dbsthd2459.datingapp.message.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dbsthd2459.datingapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: "No Title"
        val body = message.notification?.body ?: "No Body"

        createNotificationChannel()
        sendNotification(title, body)

        // 만약 채팅 알림 시 상대방의 채팅 새로고침
        if (title != "알림 메세지" && title != "매칭완료") {
            // LocalBroadcastManager를 사용하여 메시지 전달
            val broadcaster = LocalBroadcastManager.getInstance(this)
            val intent = Intent("refresh")
            broadcaster.sendBroadcast(intent)
        }

    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val soundUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.notice)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val existingChannel = notificationManager.getNotificationChannel("Chat_Channel")
            if (existingChannel == null) {
                // 채널 생성 코드
                val channel = NotificationChannel("Chat_Channel", name, importance).apply {
                    description = descriptionText
                    enableVibration(true)
                    setSound(soundUri, audioAttributes)
                }
                notificationManager.createNotificationChannel(channel)
            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(title : String, body: String){

        val builder = NotificationCompat.Builder(this, "Chat_Channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(123, builder.build())
        }
    }

}