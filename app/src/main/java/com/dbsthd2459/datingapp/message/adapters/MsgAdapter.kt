package com.dbsthd2459.datingapp.message.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.message.MsgModel
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.dbsthd2459.datingapp.utils.LocalDateTimeUtils.Companion.toLocalDateTime
import com.dbsthd2459.datingapp.utils.MyInfo
import java.time.format.DateTimeFormatter

class MsgAdapter(val context : Context, val items : MutableList<MsgModel>, val targetNickname: String, val maxWidth: Int) : RecyclerView.Adapter<MsgAdapter.MsgViewHolder>() {

    inner class MsgViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: LinearLayout = view.findViewById(R.id.msgLayout)
        val nicknameArea: TextView = view.findViewById(R.id.msgNicknameArea)
        val dateArea: TextView = view.findViewById(R.id.msgDateArea)
        val contentArea: TextView = view.findViewById(R.id.msgContentArea)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_message_view, parent, false)
        return MsgViewHolder(view)
    }

    @SuppressLint("RtlHardcoded")
    override fun onBindViewHolder(holder: MsgViewHolder, position: Int) {
        val msg = items[position]
        val uid = FirebaseAuthUtils.getUid()

        // 메시지의 정렬
        if (uid != msg.senderUid) {
            holder.layout.gravity = Gravity.LEFT
            holder.nicknameArea.text = targetNickname
        } else {
            holder.layout.gravity = Gravity.RIGHT
            holder.nicknameArea.text = MyInfo.myNickname
        }

        // 메시지 내용 및 시간 설정
        holder.dateArea.text = msg.sendDate.toLocalDateTime().format(DateTimeFormatter.ofPattern("a h:mm"))
        holder.contentArea.text = msg.sendTxt

        // msg 영역 최대 width 설정
        holder.contentArea.post {
            val params = holder.contentArea.layoutParams as LinearLayout.LayoutParams
            val maxAllowedWidth = (maxWidth * 0.6).toInt()
            val nicknameWidth = holder.nicknameArea.measuredWidth
            val contentWidth = holder.contentArea.measuredWidth

            if (contentWidth + nicknameWidth > maxAllowedWidth) {
                params.width = maxAllowedWidth - nicknameWidth
            } else {
                params.width = LinearLayout.LayoutParams.WRAP_CONTENT
            }
            holder.contentArea.layoutParams = params
        }
    }

    override fun getItemCount(): Int = items.size

}