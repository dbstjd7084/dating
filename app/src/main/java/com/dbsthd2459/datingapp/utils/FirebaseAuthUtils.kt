package com.dbsthd2459.datingapp.utils

import android.annotation.SuppressLint
import android.util.Log
import com.bumptech.glide.Glide
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.auth.UserDataModel
import com.dbsthd2459.datingapp.utils.MyInfo.Companion.myNickname
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.InputStream

class FirebaseAuthUtils {

    companion object {

        private lateinit var auth : FirebaseAuth

        private lateinit var uid: String
        private lateinit var token: String

        fun getUid() : String {

            if (::uid.isInitialized) return uid

            auth = FirebaseAuth.getInstance()

            return auth.currentUser?.uid.toString()

        }

        fun getNickname(targetUid: String, callback: (String) -> Unit) {
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val nickname = dataSnapshot.value.toString()
                    // 콜백을 통해 nickname 전달
                    callback(nickname)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("FirebaseAuthUtils", "loadPost:onCancelled", databaseError.toException())
                }
            }
            FirebaseRef.userInfoRef.child(targetUid).child("nickname").addListenerForSingleValueEvent(postListener)
        }

        // 알림 전송용 기기 토큰 가져오기
        fun getToken(targetUid: String, callback: (String) -> Unit) {
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val token = dataSnapshot.value.toString()
                    callback(token)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("FirebaseAuthUtils", "loadPost:onCancelled", databaseError.toException())
                }
            }
            FirebaseRef.userInfoRef.child(targetUid).child("token").addListenerForSingleValueEvent(postListener)
        }

        suspend fun getAccessToken(inputStream: InputStream): String? {

            if (::token.isInitialized) return token

            return withContext(Dispatchers.IO) {
                try {
                    val credentials = GoogleCredentials.fromStream(inputStream)
                        .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
                    credentials.refreshIfExpired()
                    token = credentials.accessToken.tokenValue
                    token
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }
}