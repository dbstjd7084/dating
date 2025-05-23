package com.dbsthd2459.datingapp.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseRef {

    companion object {

        val database = Firebase.database

        val userInfoRef = database.getReference("userInfo")
        val userLikeRef = database.getReference("userLike")
        val userBeLikedRef = database.getReference("userBeLiked")
        val userMsgRef = database.getReference("userMsg")
        val userPushRef = database.getReference("userPush")

    }
}