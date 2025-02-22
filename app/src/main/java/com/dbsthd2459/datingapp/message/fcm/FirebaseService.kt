package com.dbsthd2459.datingapp.message.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.dbsthd2459.datingapp.MainActivity
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.ktx.storage
import okhttp3.internal.notify


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: "No Title"
        val body = message.notification?.body ?: "No Body"

        createNotificationChannel()
        sendNotification2(title, body)

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
            .setSmallIcon(R.drawable.ok)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setWhen(System.currentTimeMillis()) // 알림 등록 시간 지정
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.app_icon))

        with(NotificationManagerCompat.from(this)) {
            notify(123, builder.build())
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification2(title : String, body: String){

        val storageRef = Firebase.storage.reference.child(FirebaseAuthUtils.getUid() + ".png")

        storageRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 프로필 이미지 URL을 가져옴
                val imageUrl = task.result

                // Glide로 이미지를 로드하고 Bitmap으로 변환하여 알림에 설정
                Glide.with(baseContext)
                    .asBitmap()  // Bitmap으로 변환
                    .load(imageUrl)
                    .circleCrop()  // 원형 이미지로 크롭
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            // 이미지 다운로드 완료 후 알림에 적용
                            val builder = NotificationCompat.Builder(baseContext, "Chat_Channel")
                                .setSmallIcon(R.drawable.ok)
                                .setContentTitle(title)
                                .setContentText(body)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setWhen(System.currentTimeMillis()) // 알림 등록 시간 지정
                                .setLargeIcon(resource)
                                .setStyle(NotificationCompat.BigTextStyle().bigText(body))

                            with(NotificationManagerCompat.from(baseContext)) {
                                notify(123, builder.build())
                            }
                        }
                    })
            } else {
                // 이미지 다운로드 실패 처리
                Toast.makeText(baseContext, "이미지 다운로드 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

}