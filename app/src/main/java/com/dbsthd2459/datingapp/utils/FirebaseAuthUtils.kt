package com.dbsthd2459.datingapp.utils

import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class FirebaseAuthUtils {

    companion object {

        private lateinit var token: String

        fun getUid() : String {

            return FirebaseAuth.getInstance().currentUser?.uid.toString()

        }

        fun getEmail(): String? {

            return FirebaseAuth.getInstance().currentUser?.email.toString()

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