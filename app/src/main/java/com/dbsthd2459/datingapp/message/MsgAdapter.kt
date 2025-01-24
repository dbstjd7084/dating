package com.dbsthd2459.datingapp.message

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.dbsthd2459.datingapp.utils.LocalDateTimeUtils.Companion.toLocalDateTime
import com.dbsthd2459.datingapp.utils.MyInfo
import java.time.format.DateTimeFormatter

class MsgAdapter(val context : Context, val items : MutableList<MsgModel>, val targetNickname: String, val maxWidth: Int) : BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var cView = convertView
        if (cView == null) {

            cView = LayoutInflater.from(parent?.context).inflate(R.layout.item_message_view, parent, false)

        }

        val layout = cView!!.findViewById<LinearLayout>(R.id.msgLayout)
        val nicknameArea = cView.findViewById<TextView>(R.id.msgNicknameArea)
        val dateArea = cView.findViewById<TextView>(R.id.msgDateArea)
        val contentArea = cView.findViewById<TextView>(R.id.msgContentArea)

        val uid = FirebaseAuthUtils.getUid()

        if (uid != items[position].senderUid) {
            layout.gravity = Gravity.LEFT
            nicknameArea.text = targetNickname
        } else {
            nicknameArea.text = MyInfo.myNickname
        }
        dateArea.text = items[position].sendDate.toLocalDateTime().format(DateTimeFormatter.ofPattern("a h:mm"))
        contentArea.text = items[position].sendTxt

        // msg 영역 최대 width 설정
        contentArea.post {
            val params = contentArea.layoutParams as LinearLayout.LayoutParams
            val maxAllowedWidth = (maxWidth * 0.6).toInt()
            val nicknameWidth = nicknameArea.measuredWidth
            val contentWidth = contentArea.measuredWidth

            if (contentWidth + nicknameWidth > maxAllowedWidth) {
                params.width = maxAllowedWidth - nicknameWidth
            } else {
                params.width = LinearLayout.LayoutParams.WRAP_CONTENT
            }
            contentArea.layoutParams = params
        }

        return cView

    }
}