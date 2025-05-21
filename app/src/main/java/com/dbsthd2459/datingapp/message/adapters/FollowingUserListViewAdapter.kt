package com.dbsthd2459.datingapp.message.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.auth.UserDataModel
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.dbsthd2459.datingapp.utils.FirebaseRef
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class FollowingUserListViewAdapter(
    private val context : Context,
    private val items : MutableList<UserDataModel>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<FollowingUserListViewAdapter.ViewHolder>() {

    private val uid = FirebaseAuthUtils.getUid()

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val profileImage = itemView.findViewById<ImageView>(R.id.profileImageArea)
        val nickname = itemView.findViewById<TextView>(R.id.listViewItemNickname)
        val followCancelBtn = itemView.findViewById<Button>(R.id.followCancelBtn)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition) // 클릭 이벤트 전달
            }
        }

        fun bind(user: UserDataModel) {
            nickname.text = user.nickname

            val targetUid = user.uid!!
            val storageRef = Firebase.storage.reference.child("$targetUid.png")
            storageRef.downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 프로필 이미지 설정
                    Glide.with(context)
                        .load(task.result)
                        .circleCrop()
                        .into(profileImage)
                } else {
                    // 이미지 다운로드 실패 처리
                    Toast.makeText(context, "이미지 다운로드 실패", Toast.LENGTH_SHORT).show()
                }
            }

            followCancelBtn.setOnClickListener {
                if (followCancelBtn.text.equals("팔로우")) {
                    FirebaseRef.userLikeRef.child(uid).child(targetUid).setValue("true")
                    FirebaseRef.userBeLikedRef.child(targetUid).child(uid).setValue("true")

                    followCancelBtn.text = "팔로우 취소"
                } else {
                    FirebaseRef.userLikeRef.child(uid).child(targetUid).removeValue()
                    FirebaseRef.userBeLikedRef.child(targetUid).child(uid).removeValue()

                    followCancelBtn.text = "팔로우"
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_following_users, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}