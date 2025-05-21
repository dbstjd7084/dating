package com.dbsthd2459.datingapp.message.tabs

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.auth.UserDataModel
import com.dbsthd2459.datingapp.message.MyLikeListActivity
import com.dbsthd2459.datingapp.message.TargetPageActivity
import com.dbsthd2459.datingapp.message.adapters.FollowingUserListViewAdapter
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.dbsthd2459.datingapp.utils.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LikeFragment : Fragment(), FollowingUserListViewAdapter.OnItemClickListener {
    interface FollowingCountListener{
        fun onFollowingCountChanged(count: Int)
    }

    private val TAG = "LikeFragment"
    private val uid = FirebaseAuthUtils.getUid()

    private val likeUserListUid = mutableListOf<String>()
    private val likeUserList = mutableListOf<UserDataModel>()

    private lateinit var listviewAdapter : FollowingUserListViewAdapter
    lateinit var getterUid: String

    private lateinit var myLikeListActivity: MyLikeListActivity
    private lateinit var followingCountListener: FollowingCountListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MyLikeListActivity) {
            myLikeListActivity = context
        }
        if (context is FollowingCountListener) {
            followingCountListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_like, container, false)

        val userListView = view.findViewById<RecyclerView>(R.id.userListView)
        listviewAdapter = FollowingUserListViewAdapter(requireContext(), likeUserList, this)

        userListView.layoutManager = LinearLayoutManager(requireContext())
        userListView.adapter = listviewAdapter

        // 팔로잉 목록 가져오기
        getMyLikeList()

        return view
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

                    // 중복 시 미추가(아이템 목록 복제 방지)
                    if (likeUserListUid.contains(user?.uid.toString()) && !likeUserList.contains(user)) {
                        likeUserList.add(user!!)
                    }

                }
                if (likeUserList.size == 0) {
                    val emptyView = view?.findViewById<TextView>(R.id.emptyView)
                    emptyView?.visibility = View.VISIBLE
                } else {
                    val emptyView = view?.findViewById<TextView>(R.id.emptyView)
                    emptyView?.visibility = View.GONE
                    listviewAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.addValueEventListener(postListener)
    }

    // 클릭한 유저의 프로필 불러오기
    override fun onItemClick(position: Int) {
        val intent = Intent(context, TargetPageActivity::class.java)
        intent.putExtra("target", likeUserListUid[position])
        startActivity(intent)
    }

}