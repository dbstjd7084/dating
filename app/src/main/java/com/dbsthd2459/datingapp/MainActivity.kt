package com.dbsthd2459.datingapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dbsthd2459.datingapp.auth.UserDataModel
import com.dbsthd2459.datingapp.message.MyLikeListActivity
import com.dbsthd2459.datingapp.mypage.MyPageActivity
import com.dbsthd2459.datingapp.slider.CardStackAdapter
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.dbsthd2459.datingapp.utils.FirebasePushUtils.Companion.sendPush
import com.dbsthd2459.datingapp.utils.FirebaseRef
import com.dbsthd2459.datingapp.utils.FirebaseRef.Companion.userInfoRef
import com.dbsthd2459.datingapp.utils.MyInfo
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    lateinit var cardStackAdapter : CardStackAdapter
    lateinit var manager : CardStackLayoutManager

    private val TAG = "MainActivity"

    private val usersDataList = mutableListOf<UserDataModel>()

    private var userCount = 0

    private lateinit var currentUserGender: String

    private val uid = FirebaseAuthUtils.getUid()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "알림 권한에 동의하셨습니다.", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this,
                "매칭을 위해 알림 권한이 필요합니다.",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cardStackView = findViewById<CardStackView>(R.id.cardStackView)

        manager = CardStackLayoutManager(baseContext, object : CardStackListener{
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {

                if (direction == Direction.Right) {
                    userLikeOtherUser(uid, usersDataList[userCount].uid.toString())
                }

                if (direction == Direction.Left) {
                    userDislikeOtherUser(uid, usersDataList[userCount].uid.toString())
                }

                userCount = userCount + 1

                if (userCount == usersDataList.count()) {
                    getUserDataList(currentUserGender)
                    Toast.makeText(this@MainActivity, "유저 정보를 새로 받아옵니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCardRewound() {

            }

            override fun onCardCanceled() {

            }

            override fun onCardAppeared(view: View?, position: Int) {

            }

            override fun onCardDisappeared(view: View?, position: Int) {

            }

        })
        // 스와이프 방향 좌우로 지정
        manager.setDirections(listOf(Direction.Left, Direction.Right))

        cardStackAdapter = CardStackAdapter(baseContext, usersDataList)
        cardStackView.layoutManager = manager
        cardStackView.adapter = cardStackAdapter

        getMyUserData()

        // Notification 권한 묻기
        askNotificationPermission()

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

    private fun askNotificationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun getMyUserData() {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val data = dataSnapshot.getValue(UserDataModel::class.java)

                currentUserGender = data?.gender.toString()

                MyInfo.myNickname = data?.nickname.toString()

                getUserDataList(currentUserGender)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelleed", databaseError.toException())
            }
        }
        userInfoRef.child(uid).addValueEventListener(postListener)
    }

    private fun getUserDataList(currentUserGender : String) {

        val postListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                dataSnapshot.children.shuffled().take(10).forEach {
                    val user = it.getValue(UserDataModel::class.java)

                    if (user!!.gender.toString() != currentUserGender) usersDataList.add(user)
                }

                cardStackAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        userInfoRef.addValueEventListener(postListener)
    }

    private fun userLikeOtherUser(myUid: String, otherUid: String) {

        FirebaseRef.userLikeRef.child(myUid).child(otherUid).setValue("true")
        FirebaseRef.userBeLikedRef.child(otherUid).child(myUid).setValue("true")

        getOtherUserLikeList(otherUid)

    }

    private fun userDislikeOtherUser(myUid: String, otherUid: String) {

        FirebaseRef.userLikeRef.child(myUid).child(otherUid).removeValue()
        FirebaseRef.userBeLikedRef.child(otherUid).child(myUid).removeValue()

    }

    // 내가 좋아요 한 사람의 좋아요 리스트 확인
    private fun getOtherUserLikeList(otherUid: String) {
        lifecycleScope.launch {
            // 매칭되었는지 확인 후 알림
            val matchSnapshot = FirebaseRef.userLikeRef.child(otherUid).child(uid).get().await()
            if (matchSnapshot.exists()) {
                Toast.makeText(this@MainActivity, "상대방도 나를 좋아하고 있어요!", Toast.LENGTH_SHORT).show()
                createNotificationChannel()
                sendMatchNotification()
            }

            // 상대방에게 푸시 알림 보내기
            val todayPushed = checkIfTodayPushed(otherUid)
            if (todayPushed) {
                Toast.makeText(this@MainActivity, "이미 푸시 알림을 보냈습니다.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // 푸시 알림 전송 및 날짜 기록
            FirebaseRef.userPushRef.child(uid).child(otherUid).setValue(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt())

            // 상대방 데이터 가져오기
            val userDataSnapshot = FirebaseRef.userInfoRef.child(otherUid).get().await()
            val data = userDataSnapshot.getValue(UserDataModel::class.java)

            // 토큰이 없는 로그아웃 상태 시 중단
            if (data!!.token == "") {
                return@launch
            }

            sendPush(uid, "${MyInfo.myNickname} 님이 당신을 좋아해요", data.token.toString(), resources.openRawResource(R.raw.service_account))
        }
    }

    // 푸시 알림을 이미 보냈는지 확인하는 suspend 함수
    private suspend fun checkIfTodayPushed(otherUid: String): Boolean {
        return withContext(Dispatchers.IO) {
            val todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
            val lastPushDateSnapshot = FirebaseRef.userPushRef.child(uid).child(otherUid).get().await()
            val lastPushDate = lastPushDateSnapshot.getValue(Int::class.java) ?: 0
            todayDate <= lastPushDate
        }
    }


    // Notification

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

            val existingChannel = notificationManager.getNotificationChannel("Main_Channel")
            if (existingChannel == null) {
                // 채널 생성 코드
                val channel = NotificationChannel("Main_Channel", name, importance).apply {
                    description = descriptionText
                    enableVibration(true)
                    setSound(soundUri, audioAttributes)
                }
                notificationManager.createNotificationChannel(channel)
            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun sendMatchNotification() {

        val builder = NotificationCompat.Builder(this, "Main_Channel")
            .setSmallIcon(R.drawable.ok)
            .setContentTitle("매칭완료")
            .setContentText("매칭이 완료되었습니다. 저 사람도 나를 좋아해요!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(123, builder.build())
        }
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "알림 설정에 동의하셨습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "매칭을 위해 알림 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}