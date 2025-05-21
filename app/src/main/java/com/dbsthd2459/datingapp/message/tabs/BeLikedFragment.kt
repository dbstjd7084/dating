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
import com.dbsthd2459.datingapp.message.adapters.FollowerListViewAdapter
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.dbsthd2459.datingapp.utils.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BeLikedFragment : Fragment(), FollowerListViewAdapter.OnItemClickListener {

    interface FollowerCountListener {
        fun onFollowerCountChanged(count: Int)
    }

    private val TAG = "BeLikedFragment"
    private val uid = FirebaseAuthUtils.getUid()

    private val FollowerListUid = mutableListOf<String>()
    private val FollowerList = mutableListOf<UserDataModel>()

    private lateinit var listviewAdapter : FollowerListViewAdapter
    lateinit var getterUid: String

    private lateinit var myLikeListActivity: MyLikeListActivity
    private lateinit var followerCountListener: FollowerCountListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MyLikeListActivity) {
            myLikeListActivity = context
        }
        if (context is FollowerCountListener) {
            followerCountListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_be_liked, container, false)

        val userListView = view.findViewById<RecyclerView>(R.id.userListView)
        listviewAdapter = FollowerListViewAdapter(requireContext(), FollowerList, this)

        userListView.layoutManager = LinearLayoutManager(requireContext())
        userListView.adapter = listviewAdapter

        getMyFollowerList()

        return view
    }

    private fun getMyFollowerList() {

        val postListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                FollowerListUid.clear()

                for (dataModel in dataSnapshot.children) {
                    FollowerListUid.add(dataModel.key.toString())
                }

                getUserDataList()

                followerCountListener.onFollowerCountChanged(FollowerListUid.size)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userBeLikedRef.child(uid).addValueEventListener(postListener)

    }

    private fun getUserDataList() {
        val postListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                FollowerList.clear()

                for (dataModel in dataSnapshot.children) {
                    val user = dataModel.getValue(UserDataModel::class.java)

                    if (FollowerListUid.contains(user?.uid.toString()) && !FollowerList.contains(user)) {
                        FollowerList.add(user!!)
                    }
                }

                if (isAdded) {
                    listviewAdapter.notifyDataSetChanged()

                    val emptyView = requireView().findViewById<TextView>(R.id.emptyView)
                    emptyView.visibility = if (FollowerList.isEmpty()) View.VISIBLE else View.GONE
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
        intent.putExtra("target", FollowerListUid[position])
        startActivity(intent)
    }

}