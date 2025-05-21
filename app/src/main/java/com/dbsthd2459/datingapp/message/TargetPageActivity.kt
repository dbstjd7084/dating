package com.dbsthd2459.datingapp.message

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dbsthd2459.datingapp.MainActivity
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.auth.UserDataModel
import com.dbsthd2459.datingapp.mypage.MyPageActivity
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.dbsthd2459.datingapp.utils.FirebasePushUtils.Companion.sendPush
import com.dbsthd2459.datingapp.utils.FirebaseRef
import com.dbsthd2459.datingapp.utils.MyInfo
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TargetPageActivity : AppCompatActivity() {

    private val TAG = "TargetPageActivity"

    lateinit var target: String

    private lateinit var myImage: ImageView // 프로필 이미지

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target_page)

        // 타겟 uid 가져오기
        target = intent.getStringExtra("target")!!

        myImage = findViewById(R.id.myImage)

        getTargetData(target)

        // 네비게이션 바 클릭 시 이벤트 설정
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_chat -> {
                    val intent = Intent(this, MyLikeListActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_matching -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }

    }

    private fun getTargetData(uid: String) {

        val myEmail = findViewById<TextView>(R.id.myEmail)
        val myNickname = findViewById<TextView>(R.id.myNickname)
        val myAge = findViewById<TextView>(R.id.myAge)
        val myCity = findViewById<TextView>(R.id.myCity)
        val myGender = findViewById<TextView>(R.id.myGender)
        val myIntroducingComment = findViewById<TextView>(R.id.myIntroducingComment)
        val myUid = FirebaseAuthUtils.getUid()

        val postListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val data = dataSnapshot.getValue(UserDataModel::class.java)

                if (data == null) {
                    Toast.makeText(this@TargetPageActivity, "데이터를 찾지 못했습니다.", Toast.LENGTH_SHORT).show()
                    return
                }

                myEmail.text = data.email
                myNickname.text = data.nickname
                myAge.text = data.age + "세"
                myGender.text = if (data.gender == "M") "남성" else "여성"
                myCity.text = data.city
                myIntroducingComment.text = data.comment ?: "작성된 소개말이 없습니다."

                val storageRef = Firebase.storage.reference.child(uid + ".png")

                storageRef.downloadUrl.addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        // 프로필 이미지 설정
                        Glide.with(baseContext)
                            .load(task.result)
                            .circleCrop()
                            .into(myImage)

                    } else {
                        // 이미지 다운로드 실패 처리
                        Toast.makeText(baseContext, "이미지 다운로드 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                val pushBtn = findViewById<Button>(R.id.pushBtn)
                pushBtn.setOnClickListener {
                    // 서로 팔로잉이 되어있는 경우 중단
                    lifecycleScope.launch {
                        if (checkIfTodayPushed(uid)) {
                            Toast.makeText(this@TargetPageActivity, "이미 오늘 상대에게 푸시 알림을 보냈습니다.", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        FirebaseRef.userBeLikedRef.child(myUid).child(uid).get().addOnSuccessListener { snapshot ->
                            if (snapshot.exists()) {
                                // 서로 팔로잉 중일 경우
                                Toast.makeText(this@TargetPageActivity, "이미 매칭되었거나 팔로우하지 않았습니다.", Toast.LENGTH_LONG).show()
                                return@addOnSuccessListener
                            }

                            Toast.makeText(this@TargetPageActivity, "상대방에게 관심을 표했어요", Toast.LENGTH_LONG).show()

                            // 상대방의 알림 차단 목록에 있는 경우 중단

                            // 하루에 한 번만 알림 가능

                            // 토큰이 없는 로그아웃 상태 시 중단
                            if (data.token == "") {
                                return@addOnSuccessListener
                            }
                            lifecycleScope.launch {
                                sendPush(myUid, "${MyInfo.myNickname} 님께서 푸시 알림을 보냈습니다!", data.token.toString(), resources.openRawResource(R.raw.service_account))
                            }
                            FirebaseRef.userPushRef.child(myUid).child(uid).setValue(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt())
                        }
                    }
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.child(uid).addValueEventListener(postListener)
    }

    private suspend fun checkIfTodayPushed(otherUid: String): Boolean {
        return withContext(Dispatchers.IO) {
            val todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
            val lastPushDateSnapshot = FirebaseRef.userPushRef.child(FirebaseAuthUtils.getUid()).child(otherUid).get().await()
            val lastPushDate = lastPushDateSnapshot.getValue(Int::class.java) ?: 0
            todayDate <= lastPushDate
        }
    }

}