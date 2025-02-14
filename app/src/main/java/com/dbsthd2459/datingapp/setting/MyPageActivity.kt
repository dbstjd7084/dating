package com.dbsthd2459.datingapp.setting

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dbsthd2459.datingapp.MainActivity
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.auth.IntroActivity
import com.dbsthd2459.datingapp.auth.UserDataModel
import com.dbsthd2459.datingapp.message.MyLikeListActivity
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.dbsthd2459.datingapp.utils.FirebaseRef
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyPageActivity : AppCompatActivity() {

    private val TAG = "MyPageActivity"

    private val uid = FirebaseAuthUtils.getUid()

    private lateinit var editText: TextInputEditText // 소개말 작성창
    private lateinit var completeBtn: TextView // 확인 버튼
    private var completeIsEnabled = false // 확인 버튼 활성화 여부
    private var initComment = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        getMyData()

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        logoutBtn.setOnClickListener {

            val auth = Firebase.auth
            auth.signOut()

            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 네비게이션 바 클릭 시 이벤트 설정
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_chat -> {
                    val intent = Intent(this, MyLikeListActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_matching -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }

        // 소개말 작성 이벤트
        editText = findViewById<TextInputEditText>(R.id.introducingCommentEdit)
        completeBtn = findViewById<TextView>(R.id.completeBtn) // 확인 글자
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                // 작성된 내용이 있다면
                if (!s.isNullOrEmpty()) {
                    // 확인 글자가 비활성화 상태라면
                    if (!completeIsEnabled) {
                        // 확인 글자 활성화
                        completeIsEnabled = true
                        completeBtn.setTextColor(ContextCompat.getColor(this@MyPageActivity, R.color.dark_white))
                    } else {
                        // 확인 글자가 활성화 상태일 경우 기존 소개말과 비교
                        if (s.toString().equals(initComment)) {
                            completeIsEnabled = false
                            completeBtn.setTextColor(ContextCompat.getColor(this@MyPageActivity, R.color.dark_gray))
                        }
                    }
                } else {
                    if (completeIsEnabled) {
                        // 초기 설정에 내용이 있다면
                        if (initComment.isNotEmpty()) {
                            // 확인 글자 활성화
                            completeIsEnabled = true
                            completeBtn.setTextColor(ContextCompat.getColor(this@MyPageActivity, R.color.dark_white))
                        } else {
                            // 확인 글자 비활성화
                            completeIsEnabled = false
                            completeBtn.setTextColor(ContextCompat.getColor(this@MyPageActivity, R.color.dark_gray))
                        }
                    }
                }
            }
        })

        // 소개말 작성 변수 선언
        val layout = findViewById<FrameLayout>(R.id.editTextLayout)
        // 확인 글자 클릭 이벤트
        completeBtn.setOnClickListener {
            if (completeIsEnabled) {
                FirebaseRef.userInfoRef.child(uid).child("comment").setValue(editText.text.toString())
                // 편집창 숨기기
                layout.visibility = View.GONE
                // 네비게이션 바 표시
                bottomNavigation.visibility = View.VISIBLE
                // 확인 글자 비활성화
                completeIsEnabled = false
                completeBtn.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
                editText.clearFocus()
                // 표시
                getMyData()
            }
        }

        // 취소 글자 클릭 이벤트
        val cancelBtn = findViewById<TextView>(R.id.cancelBtn)
        cancelBtn.setOnClickListener {
            // 편집창 숨기기
            layout.visibility = View.GONE
            // 네비게이션 바 표시
            bottomNavigation.visibility = View.VISIBLE
            // 확인 글자 비활성화
            completeIsEnabled = false
            completeBtn.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
            editText.clearFocus()
        }

        // 소개말 클릭 시 이벤트
        val introducingComment = findViewById<TextView>(R.id.myIntroducingComment)
        introducingComment.setOnClickListener {
            // 편집창 띄우기
            layout.visibility = View.VISIBLE
            // 네비게이션 바 숨기기
            bottomNavigation.visibility = View.GONE
            // 소개말 초기 설정
            editText.setText(initComment)
            // 확인 글자 비활성화
            completeIsEnabled = false
            completeBtn.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
            // 편집 레이아웃 포커싱
            editText.requestFocus()
            if (!editText.text.isNullOrEmpty()) editText.setSelection(editText.text!!.length)
        }
    }

    private fun getMyData() {

        val myImage = findViewById<ImageView>(R.id.myImage)

        val myEmail = findViewById<TextView>(R.id.myEmail)
        val myNickname = findViewById<TextView>(R.id.myNickname)
        val myAge = findViewById<TextView>(R.id.myAge)
        val myCity = findViewById<TextView>(R.id.myCity)
        val myGender = findViewById<TextView>(R.id.myGender)
        val myIntroducingComment = findViewById<TextView>(R.id.myIntroducingComment)

        val postListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val data = dataSnapshot.getValue(UserDataModel::class.java)

                myEmail.text = FirebaseAuthUtils.getEmail()
                myNickname.text = data!!.nickname
                myAge.text = data.age + "세"
                if (data.gender == "M") myGender.text = "남성"
                else myGender.text = "여성"
                myCity.text = data.city
                if (data.comment.isNullOrBlank()) {
                    myIntroducingComment.text = "소개말을 작성해주세요."
                    editText.setText(data.comment)
                    initComment = ""
                }
                else {
                    myIntroducingComment.text = data.comment
                    editText.setText(data.comment)
                    initComment = data.comment
                    completeIsEnabled = false
                }

                val storageRef = Firebase.storage.reference.child(data.uid + ".png")

                storageRef.downloadUrl.addOnCompleteListener({ task ->
                    if (task.isSuccessful) {
                        Glide.with(baseContext)
                            .load(task.result)
                            .circleCrop()
                            .into(myImage)
                    }
                })


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.child(uid).addValueEventListener(postListener)
    }
}