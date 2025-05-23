package com.dbsthd2459.datingapp.slider

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.auth.UserDataModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class CardStackAdapter(val context : Context, val items : List<UserDataModel>) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardStackAdapter.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view : View = inflater.inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: CardStackAdapter.ViewHolder, position: Int) {
        holder.binding(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.profileImageArea)
        val nickname = itemView.findViewById<TextView>(R.id.itemNickname)
        val age = itemView.findViewById<TextView>(R.id.itemAge)
        val city = itemView.findViewById<TextView>(R.id.itemCity)
        val comment = itemView.findViewById<TextView>(R.id.itemComment)

        @SuppressLint("SetTextI18n")
        fun binding(data : UserDataModel) {

            val storageRef = Firebase.storage.reference.child(data.uid + ".png")

            storageRef.downloadUrl.addOnCompleteListener({ task ->
                if (task.isSuccessful) {
                    Glide.with(context)
                        .load(task.result)
                        .into(image)
                }
            })

            nickname.text = data.nickname + ","
            age.text = data.age
            city.text = data.city
            if (data.comment.isNullOrEmpty()) comment.text = "작성된 소개말이 없어요!"
            else {
                comment.text = data.comment
                comment.maxLines = 3
                comment.ellipsize = android.text.TextUtils.TruncateAt.END
            }

        }

    }
}