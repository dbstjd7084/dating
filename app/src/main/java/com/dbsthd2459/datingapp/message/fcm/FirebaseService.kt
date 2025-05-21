package com.dbsthd2459.datingapp.message.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dbsthd2459.datingapp.MainActivity
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.message.ChatActivity
import com.dbsthd2459.datingapp.message.MyLikeListActivity
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.ktx.storage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: "No Title"
        val body = message.notification?.body ?: "No Body"

        // 알림
        createNotificationChannel()

        // 채팅 알림인 경우
        if (title != "매칭완료") {
            // 푸시 알림이 아니라면 ex) 채팅 알림
            if (!body.contains(" 님께서 푸시 알림을 보냈습니다!")) {
                // LocalBroadcastManager를 사용하여 채팅 화면 갱신
                val broadcaster = LocalBroadcastManager.getInstance(this)
                val intent = Intent("refresh")
                broadcaster.sendBroadcast(intent)
            }

            // 알림
            sendNotificationForMsg(title, body)
        } else {
            // 매칭 완료 알림인 경우
            sendNotification(title, body)
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
    private fun sendNotificationForMsg(uid : String, body: String){

        val storageRef = Firebase.storage.reference.child("$uid.png")

        storageRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 프로필 이미지 URL을 가져옴
                val imageUrl = task.result

                // Glide로 이미지를 로드하고 Bitmap으로 변환하여 알림에 설정
                Glide.with(baseContext)
                    .asBitmap()  // Bitmap으로 변환
                    .load(imageUrl)
                    .circleCrop()  // 원형 이미지로 크롭
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            // 이미지 다운로드 완료 후 닉네임 가져오기, 알림에 프로필 사진 적용
                            FirebaseAuthUtils.getNickname(uid) { nickname ->

                                /* 푸시 알림인 경우 MyLikeListActivity로,
                                채팅 알림인 경우 ChatActivity로 이동 */
                                val intent = if (body.contains(" 님께서 푸시 알림을 보냈습니다!")) Intent(baseContext, MyLikeListActivity::class.java).apply {
                                    putExtra("target", uid)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                }
                                else Intent(baseContext, ChatActivity::class.java).apply {
                                    putExtra("target", uid)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                }

                                val pendingIntent = PendingIntent.getActivity(
                                    baseContext,
                                    0,
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                )

                                var title = nickname
                                if (body.contains(" 님께서 푸시 알림을 보냈습니다!")) title = "알림 메시지"

                                val builder = NotificationCompat.Builder(baseContext, "Chat_Channel")
                                    .setSmallIcon(R.drawable.ok)
                                    .setContentTitle(title)
                                    .setContentText(body)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setWhen(System.currentTimeMillis()) // 알림 등록 시간 지정
                                    .setLargeIcon(resource) // 프로필 사진 등록
                                    .setStyle(NotificationCompat.BigTextStyle().bigText(body)) // 긴 메시지도 출력
                                    .setAutoCancel(true) // 클릭 시 알림 삭제
                                    .setContentIntent(pendingIntent) // 클릭 시 대화방 띄우기

                                with(NotificationManagerCompat.from(baseContext)) {
                                    notify(123, builder.build())
                                }
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
            } else {
                // 이미지 다운로드 실패 처리
                Toast.makeText(baseContext, "이미지 다운로드 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(title : String, body: String){

        // 이미지 다운로드 완료 후 닉네임 가져오기, 알림에 프로필 사진 적용
        FirebaseAuthUtils.getNickname(title) { nickname ->

            val intent = Intent(baseContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            val pendingIntent = PendingIntent.getActivity(
                baseContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(baseContext, "Chat_Channel")
                .setSmallIcon(R.drawable.ok)
                .setContentTitle(nickname)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setWhen(System.currentTimeMillis()) // 알림 등록 시간 지정
                .setStyle(NotificationCompat.BigTextStyle().bigText(body)) // 긴 메시지도 출력
                .setAutoCancel(true) // 클릭 시 알림 삭제
                .setContentIntent(pendingIntent) // 클릭 시 대화방 띄우기

            with(NotificationManagerCompat.from(baseContext)) {
                notify(123, builder.build())
            }
        }

    }

}