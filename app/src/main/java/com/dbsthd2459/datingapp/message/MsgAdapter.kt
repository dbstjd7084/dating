package com.dbsthd2459.datingapp.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.dbsthd2459.datingapp.R

class MsgAdapter(val context : Context, val items : MutableList<MsgModel>) : BaseAdapter() {
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

            cView = LayoutInflater.from(parent?.context).inflate(R.layout.item_list_view, parent, false)

        }

        val nicknameArea = cView!!.findViewById<TextView>(R.id.listViewItemNicknameArea)
        val textArea = cView.findViewById<TextView>(R.id.listViewItemNickname)

        nicknameArea.text = items[position].senderInfo
        textArea.text = items[position].sendTxt

        return cView

    }
}