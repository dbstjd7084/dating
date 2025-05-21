package com.dbsthd2459.datingapp.utils

import android.util.Log
import com.dbsthd2459.datingapp.utils.FirebaseRef.Companion.userBeLikedRef
import com.dbsthd2459.datingapp.utils.FirebaseRef.Companion.userInfoRef
import com.dbsthd2459.datingapp.utils.FirebaseRef.Companion.userLikeRef
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthUtils {

    companion object {

        fun getUid() : String {

            return FirebaseAuth.getInstance().currentUser?.uid.toString()

        }

        fun getEmail(): String {

            return FirebaseAuth.getInstance().currentUser?.email.toString()

        }

        fun getEmail(targetUid: String, callback: (String) -> Unit) {

            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val email = dataSnapshot.value.toString()
                    // 콜백을 통해 email 전달
                    callback(email)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("FirebaseAuthUtils", "loadPost:onCancelled", databaseError.toException())
                }
            }
            userInfoRef.child(targetUid).child("email").addListenerForSingleValueEvent(postListener)
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
            userInfoRef.child(targetUid).child("nickname").addListenerForSingleValueEvent(postListener)
        }

        // 팔로잉 수 가져오기
        suspend fun getFollowingCount(): Long {
            return suspendCoroutine { continuation ->
                val postListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val count = dataSnapshot.childrenCount
                        continuation.resume(count)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w("FirebaseAuthUtils", "loadPost:onCancelled", databaseError.toException())
                        continuation.resume(0)
                    }
                }
                userLikeRef.child(getUid()).addListenerForSingleValueEvent(postListener)
            }
        }

        // 팔로워 수 가져오기
        suspend fun getFollowerCount(): Long {
            return suspendCoroutine { continuation ->
                val postListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val count = dataSnapshot.childrenCount
                        continuation.resume(count)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w("FirebaseAuthUtils", "loadPost:onCancelled", databaseError.toException())
                        continuation.resume(0)
                    }
                }
                userBeLikedRef.child(getUid()).addListenerForSingleValueEvent(postListener)
            }
        }

        fun refreshToken() {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 최신 토큰을 가져옴
                    val newToken = task.result
                    Log.d("FCM", "New Token: $newToken")

                    // Firebase에 저장된 사용자 UID 가져오기
                    val uid = FirebaseAuth.getInstance().currentUser?.uid

                    // UID가 null이 아니면 토큰을 저장
                    if (uid != null) {
                        saveTokenToDatabase(newToken)
                    } else {
                        Log.e("FCM", "UID is null, cannot save token")
                    }
                } else {
                    Log.e("FCM", "Fetching FCM token failed", task.exception)
                }
            }
        }

        // Firebase Realtime Database에 토큰 저장
        private fun saveTokenToDatabase(token: String) {
            userInfoRef.child(getUid()).child("token").setValue(token)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FCM", "Token successfully updated to Firebase DB")
                    } else {
                        Log.e("FCM", "Failed to update token in Firebase DB", task.exception)
                    }
                }
        }

        // Uid의 기기 토큰 가져오기
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
            userInfoRef.child(targetUid).child("token").addListenerForSingleValueEvent(postListener)
        }

        suspend fun getAccessToken(inputStream: InputStream): String? {

            return withContext(Dispatchers.IO) {
                try {
                    val credentials = GoogleCredentials.fromStream(inputStream)
                        .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
                    credentials.refreshIfExpired()
                    // 출력
                    credentials.accessToken.tokenValue
                } catch (e: Exception) {
                    Log.e("FCM", "액세스 토큰 획득 중 오류 발생: ${e.message}")
                    e.printStackTrace()
                    null
                }
            }
        }
    }
}