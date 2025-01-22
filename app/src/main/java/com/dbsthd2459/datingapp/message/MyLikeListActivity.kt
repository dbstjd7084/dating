package com.dbsthd2459.datingapp.message

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.auth.UserDataModel
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.dbsthd2459.datingapp.utils.FirebaseRef
import com.dbsthd2459.datingapp.utils.MyInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

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

        listviewAdapter = ListViewAdapter(this, likeUserList)
        userListView.adapter = listviewAdapter

        getMyLikeList()

        userListView.setOnItemClickListener { parent, view, position, id ->

            getterUid = likeUserList[position].uid.toString()
            getterToken = likeUserList[position].token.toString()
            checkMatching(getterUid)

        }

        userListView.setOnItemLongClickListener { parent, view, position, id ->

            Toast.makeText(this@MyLikeListActivity, "꾹 눌러서 보냈수ㄴㅇ", Toast.LENGTH_LONG).show()

            lifecycleScope.launch {

                sendPush("알림 메시지", MyInfo.myNickname + " 님께서 푸시 알림을 보냈습니다!", likeUserList[position].token.toString())

            }

            return@setOnItemLongClickListener(true)
        }

    }

    // PUSH 보내기
    private suspend fun sendPush(title: String, content: String, token: String) {

        val client = OkHttpClient()
        val accessToken = FirebaseAuthUtils.getAccessToken(resources.openRawResource(R.raw.service_account))

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

    private fun checkMatching(otherUid: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.children.count() == 0) {
                    Toast.makeText(this@MyLikeListActivity, "상대방이 좋아요한 사람이 아무도 없어요", Toast.LENGTH_LONG).show()
                } else {

                    for (dataModel in dataSnapshot.children) {

                        val likeUserKey = dataModel.key.toString()
                        if (likeUserKey.equals(uid)) {
                            Toast.makeText(this@MyLikeListActivity, "매칭이 되었습니다.", Toast.LENGTH_LONG).show()

                            showDialog()
                        } else {
                            Toast.makeText(this@MyLikeListActivity, "매칭이 되지 않았습니다.", Toast.LENGTH_LONG).show()
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

    private fun showDialog() {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("메세지 보내기")

        val mAlertDialog = mBuilder.show()

        val btn = mAlertDialog.findViewById<Button>(R.id.sendBtnArea)
        val textArea = mAlertDialog.findViewById<EditText>(R.id.sendTextArea)
        btn?.setOnClickListener {

            val msgText = textArea!!.text.toString()

            if (msgText.isEmpty()) {
                Toast.makeText(this, "작성된 내용이 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val msgModel = MsgModel(MyInfo.myNickname, msgText)

            FirebaseRef.userMsgRef.child(getterUid).push().setValue(msgModel)

            lifecycleScope.launch {

                sendPush(MyInfo.myNickname, msgText, getterToken)

            }

            mAlertDialog.dismiss()

            Toast.makeText(this, "쪽지를 보냈습니다.", Toast.LENGTH_SHORT).show()
        }

    }

}