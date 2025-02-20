package com.dbsthd2459.datingapp.message

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dbsthd2459.datingapp.MainActivity
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.auth.UserDataModel
import com.dbsthd2459.datingapp.mypage.MyPageActivity
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.dbsthd2459.datingapp.utils.FirebasePushUtils.Companion.sendPush
import com.dbsthd2459.datingapp.utils.FirebaseRef
import com.dbsthd2459.datingapp.utils.LocalDateTimeUtils.Companion.toTimestamp
import com.dbsthd2459.datingapp.utils.MyInfo
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MyLikeListActivity : AppCompatActivity() {

    private val TAG = "MyLikeListActivity"
    private val uid = FirebaseAuthUtils.getUid()

    private val likeUserListUid = mutableListOf<String>()
    private val likeUserList = mutableListOf<UserDataModel>()

    lateinit var listviewAdapter : ListViewAdapter
    lateinit var getterUid: String
    lateinit var getterToken: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_like_list)

        val userListView = findViewById<ListView>(R.id.userListView)
        val emptyView = findViewById<TextView>(R.id.emptyView)

        listviewAdapter = ListViewAdapter(this, likeUserList)
        userListView.emptyView = emptyView
        userListView.adapter = listviewAdapter

        getMyLikeList()

        userListView.setOnItemClickListener { parent, view, position, id ->

            getterUid = likeUserList[position].uid.toString()
            getterToken = likeUserList[position].token.toString()
            checkMatching(getterUid)

        }

        userListView.setOnItemLongClickListener { parent, view, position, id ->

            Toast.makeText(this@MyLikeListActivity, "상대방에게 관심을 표했어요", Toast.LENGTH_LONG).show()

            lifecycleScope.launch {


                sendPush("알림 메세지", MyInfo.myNickname + " 님께서 푸시 알림을 보냈습니다!", likeUserList[position].token.toString(), resources.openRawResource(R.raw.service_account))

            }

            return@setOnItemLongClickListener(true)
        }

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

    private fun checkMatching(otherUid: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.children.count() == 0) {
                    Toast.makeText(this@MyLikeListActivity, "상대방이 좋아요한 사람이 아무도 없어요", Toast.LENGTH_LONG).show()
                } else {

                    for (dataModel in dataSnapshot.children) {

                        val likeUserKey = dataModel.key.toString()
                        if (likeUserKey.equals(uid)) {

                            val intent = Intent(this@MyLikeListActivity, ChatActivity::class.java)
                            intent.putExtra("target", otherUid)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@MyLikeListActivity, "매칭이 되지 않아 채팅을 할 수 없습니다. 길게 눌러 푸시 알림을 보내보세요!", Toast.LENGTH_LONG).show()
                        }

                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        FirebaseRef.userLikeRef.child(otherUid).addValueEventListener(postListener)

    }

    private fun getMyLikeList() {

        val postListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {
                    likeUserListUid.add(dataModel.key.toString())
                }
                getUserDataList()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userLikeRef.child(uid).addValueEventListener(postListener)

    }

    private fun getUserDataList() {
        val postListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {

                    val user = dataModel.getValue(UserDataModel::class.java)
                    
                    if (likeUserListUid.contains(user?.uid.toString())) {
                        likeUserList.add(user!!)
                    }

                }
                listviewAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.addValueEventListener(postListener)
    }

}