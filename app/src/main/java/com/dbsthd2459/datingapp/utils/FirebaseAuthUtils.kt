package com.dbsthd2459.datingapp.utils

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class FirebaseAuthUtils {

    companion object {

        private lateinit var auth : FirebaseAuth

        fun getUid() : String {

            auth = FirebaseAuth.getInstance()

            return auth.currentUser?.uid.toString()

        }

        suspend fun getAccessToken(inputStream: InputStream): String? {
            return withContext(Dispatchers.IO) {
                try {
                    val credentials = GoogleCredentials.fromStream(inputStream)
                        .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
                    credentials.refreshIfExpired()
                    credentials.accessToken.tokenValue
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }
}