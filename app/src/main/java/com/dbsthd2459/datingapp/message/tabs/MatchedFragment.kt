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
import com.dbsthd2459.datingapp.message.ChatActivity
import com.dbsthd2459.datingapp.message.MyLikeListActivity
import com.dbsthd2459.datingapp.message.adapters.MatchedUserListViewAdapter
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.dbsthd2459.datingapp.utils.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MatchedFragment : Fragment(), MatchedUserListViewAdapter.OnItemClickListener {

    private val TAG = "MatchedFragment"
    private val uid = FirebaseAuthUtils.getUid()

    private val matchedListUid = mutableListOf<String>()
    private val matchedUserList = mutableListOf<UserDataModel>()

    private lateinit var listviewAdapter : MatchedUserListViewAdapter
    lateinit var getterUid: String
    lateinit var getterToken: String

    private lateinit var myLikeListActivity: MyLikeListActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MyLikeListActivity) {
            myLikeListActivity = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_matched, container, false)

        val userListView = view.findViewById<RecyclerView>(R.id.userListView)
        listviewAdapter = MatchedUserListViewAdapter(requireContext(), matchedUserList, this)

        userListView.layoutManager = LinearLayoutManager(requireContext())
        userListView.adapter = listviewAdapter

        getMyChatList()

        return view
    }

    override fun onItemClick(position: Int) {
        getterUid = matchedUserList[position].uid.toString()
        getterToken = matchedUserList[position].token.toString()
        openChatting(getterUid)
    }

    private fun openChatting(otherUid: String) {

        val intent = Intent(myLikeListActivity, ChatActivity::class.java)
        intent.putExtra("target", otherUid)
        startActivity(intent)

    }

    private fun getMyChatList() {

        // 팔로잉 리스트 가져오기
        val likeUserListUid = mutableListOf<String>()
        val postListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {
                    likeUserListUid.add(dataModel.key.toString())
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userLikeRef.child(uid).addValueEventListener(postListener)

        // 팔로워 리스트 가져오기
        // 팔로잉 리스트에 포함 시 채팅 상대 리스트 추가
        val postListener2 = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {
                    if (likeUserListUid.contains(dataModel.key.toString())) {
                        matchedListUid.add(dataModel.key.toString())
                    }
                }
                getUserDataList()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userBeLikedRef.child(uid).addValueEventListener(postListener2)

    }

    private fun getUserDataList() {
        val postListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {

                    val user = dataModel.getValue(UserDataModel::class.java)

                    if (matchedListUid.contains(user?.uid.toString()) && !matchedUserList.contains(user)) {
                        matchedUserList.add(user!!)
                    }

                }
                if (matchedUserList.size == 0) {
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

}