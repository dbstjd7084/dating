package com.dbsthd2459.datingapp.utils

import android.util.Log
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils.Companion.getAccessToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.InputStream

class FirebasePushUtils {
    companion object {

        // PUSH 보내기
        suspend fun sendPush(title: String, content: String, token: String, input: InputStream) {

            val client = OkHttpClient()
            val accessToken = getAccessToken(input)

            if (accessToken == null) {
                Log.e("FCM", "토큰 액세스에 실패했습니다.")
                return
            }

            val notification = JSONObject().apply {
                put("title", title)
                put("body", content)
            }

            val data = JSONObject().apply {
                put("messageKey", "messageValue")
            }

            val message = JSONObject().apply {
                put("token", token)
                put("notification", notification)
                put("data", data)
            }

            val json = JSONObject().apply {
                put("message", message)
            }

            val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("https://fcm.googleapis.com/v1/projects/dating-f3ba7/messages:send")
                .post(requestBody)
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .build()

            // Coroutine을 사용하여 비동기 요청 처리
            withContext(Dispatchers.IO) {
                try {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        Log.d("FCM", "Notification sent: ${response.body?.string()}")
                    } else {
                        Log.e("FCM", "Failed to send notification: ${response.code}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

    }
}