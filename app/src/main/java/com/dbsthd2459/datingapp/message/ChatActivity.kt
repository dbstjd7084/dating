package com.dbsthd2459.datingapp.message

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.dbsthd2459.datingapp.utils.FirebasePushUtils.Companion.sendPush
import com.dbsthd2459.datingapp.utils.FirebaseRef
import com.dbsthd2459.datingapp.utils.LocalDateTimeUtils.Companion.toTimestamp
import com.dbsthd2459.datingapp.utils.MyInfo
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ChatActivity : AppCompatActivity() {

    private val TAG = "ChatActivity"

    lateinit var recyclerAdapter: MsgAdapter
    var msgList = mutableListOf<MsgModel>()
    lateinit var target: String
    private lateinit var tNickname: String

    private lateinit var recyclerView: RecyclerView

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            getMyMsg()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerView = findViewById(R.id.msgListView)
        recyclerView.setHasFixedSize(true)

        // LayoutManager 설정 (세로 스크롤 기본)
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true // 메시지 리스트가 아래부터 표시
        }

        target = intent.getStringExtra("target")!!

        FirebaseAuthUtils.getNickname(target) { nickname ->

            tNickname = nickname

            val displayMetrics = resources.displayMetrics
            recyclerAdapter = MsgAdapter(this, msgList, nickname, displayMetrics.widthPixels)
            recyclerView.adapter = recyclerAdapter

            getMyMsg()

        }

        // 보내기 버튼 이벤트
        val btn = findViewById<Button>(R.id.sendBtnArea)
        val text = findViewById<EditText>(R.id.sendTextArea)
        btn.setOnClickListener {

            val msgText = text.text.toString()

            if (msgText.isEmpty()) {
                Toast.makeText(this, "작성된 내용이 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val msgModel = MsgModel(FirebaseAuthUtils.getUid(), LocalDateTime.now().toTimestamp(), msgText)

            FirebaseRef.userMsgRef.child(target).child(FirebaseAuthUtils.getUid()).push().setValue(msgModel)

            var token: String
            FirebaseAuthUtils.getToken(target) {
                token = it

                lifecycleScope.launch {

                    sendPush(MyInfo.myNickname, msgText, token, resources.openRawResource(R.raw.service_account))

                }

            }

            text.setText("")

            getMyMsg()

        }

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter("refresh"))

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getMyMsg() {

        val sentMessages = mutableListOf<MsgModel>()
        val receivedMessages = mutableListOf<MsgModel>()

        val sentMessagesTask = FirebaseRef.userMsgRef.child(target).child(FirebaseAuthUtils.getUid()).get()
        val receivedMessagesTask = FirebaseRef.userMsgRef.child(FirebaseAuthUtils.getUid()).child(target).get()

        Tasks.whenAll(sentMessagesTask, receivedMessagesTask).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 보낸 메시지
                val sentSnapshot = sentMessagesTask.result
                sentSnapshot?.children?.forEach { child ->
                    child.getValue(MsgModel::class.java)?.let { sentMessages.add(it) }
                }

                // 받은 메시지
                val receivedSnapshot = receivedMessagesTask.result
                receivedSnapshot?.children?.forEach { child ->
                    child.getValue(MsgModel::class.java)?.let { receivedMessages.add(it) }
                }

                val allMessages = sentMessages + receivedMessages
                val sortedMessages = allMessages.sortedBy { it.sendDate }

                msgList.clear()
                msgList.addAll(sortedMessages)

                recyclerAdapter.notifyDataSetChanged()

                recyclerView.scrollToPosition(msgList.size - 1)

            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

}