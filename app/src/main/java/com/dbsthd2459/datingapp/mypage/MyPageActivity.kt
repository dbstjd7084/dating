package com.dbsthd2459.datingapp.mypage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class MyPageActivity : AppCompatActivity() {

    private val TAG = "MyPageActivity"

    private val uid = FirebaseAuthUtils.getUid()

    private lateinit var editText: TextInputEditText // 소개말 작성창
    private lateinit var completeBtn: TextView // 확인 버튼
    private var completeIsEnabled = false // 확인 버튼 활성화 여부
    private var initComment = ""
    private lateinit var myImage: ImageView // 프로필 이미지

    private lateinit var interpreter: Interpreter
    private val imageSize = 224 // 이미지 크기
    private val outputBuffer = Array(1) { FloatArray(5) } // 모델의 출력 크기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        getMyData()

        // 로그아웃 버튼 이벤트
        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        logoutBtn.setOnClickListener {

            val auth = Firebase.auth
            auth.signOut()

            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
            finish()

            FirebaseRef.userInfoRef.child(uid).child("token").setValue("")
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

        // 프로필 이미지 클릭 시 프로필 이미지 재설정 띄우기
        myImage = findViewById(R.id.myImage)
        val getAction = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                val profileImage = findViewById<ImageView>(R.id.imageArea)
                profileImage.setImageURI(uri)
                // Firebase 업로드
                uploadImage(uid)
                Toast.makeText(this, "프로필 이미지를 변경했습니다.", Toast.LENGTH_SHORT).show()
                val faceScoreView = findViewById<TextView>(R.id.checkMyScoreByAI)
                faceScoreView.text = "AI가 외모 점수를 산정중입니다.."
            } else {
                // 이미지 선택이 취소된 경우 (uri가 null)
                Toast.makeText(baseContext, "이미지 선택이 취소되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        myImage.setOnClickListener {
            getAction.launch("image/*")
        }

        // 얼굴 점수 AI 모델 로드
        loadModel(assets)
    }

    private fun getMyData() {

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
                        // 프로필 이미지 설정
                        Glide.with(baseContext)
                            .load(task.result)
                            .circleCrop()
                            .into(myImage)

                        // 프로필 이미지로 AI 외모 점수 계산 및 적용
                        // Glide를 사용하여 이미지 다운로드 및 Bitmap으로 변환
                        Glide.with(baseContext)
                            .asBitmap()
                            .load(task.result)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    // Bitmap 이미지를 사용하여 얼굴 평가 모델 실행
                                    runFaceRatingModel(resource)
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {
                                    // 필요한 경우 구현
                                }
                            })
                    } else {
                        // 이미지 다운로드 실패 처리
                        Toast.makeText(baseContext, "이미지 다운로드 실패", Toast.LENGTH_SHORT).show()
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

    // 프로필 이미지 업로드
    private fun uploadImage(uid : String) {

        val storage = Firebase.storage
        val storageRef = storage.reference.child("$uid.png")

        val profileImage = findViewById<ImageView>(R.id.imageArea)
        profileImage.isDrawingCacheEnabled = true
        profileImage.buildDrawingCache()
        val bitmap = (profileImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // 업로드 완료 후 메모리 관리를 위한 액티비티 종료
            val storageRef = Firebase.storage.reference.child(uid + ".png")

            storageRef.downloadUrl.addOnCompleteListener({ task ->
                if (task.isSuccessful) {
                    // 프로필 이미지 설정
                    Glide.with(baseContext)
                        .load(task.result)
                        .circleCrop()
                        .into(myImage)

                    // 프로필 이미지로 AI 외모 점수 계산 및 적용
                    // Glide를 사용하여 이미지 다운로드 및 Bitmap으로 변환
                    Glide.with(baseContext)
                        .asBitmap()
                        .load(task.result)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                // Bitmap 이미지를 사용하여 얼굴 평가 모델 실행
                                runFaceRatingModel(resource)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                // 필요한 경우 구현
                            }
                        })
                } else {
                    // 이미지 다운로드 실패 처리
                    Toast.makeText(baseContext, "이미지 다운로드 실패", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // 모델 로드 함수 (onCreate 또는 초기화 시 호출)
    fun loadModel(assetManager: AssetManager) {
        val fileDescriptor = assetManager.openFd("Facial_rating_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        interpreter = Interpreter(modelBuffer)
    }

    // 이미지 전처리 함수
    fun preprocessImage(bitmap: Bitmap, imageSize: Int): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true)
        val inputBuffer = ByteBuffer.allocateDirect(1 * imageSize * imageSize * 3 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(imageSize * imageSize)
        resizedBitmap.getPixels(pixels, 0, imageSize, 0, 0, imageSize, imageSize)

        for (pixelValue in pixels) {
            val r = (pixelValue shr 16 and 0xFF) / 255.0f
            val g = (pixelValue shr 8 and 0xFF) / 255.0f
            val b = (pixelValue and 0xFF) / 255.0f

            inputBuffer.putFloat(r)
            inputBuffer.putFloat(g)
            inputBuffer.putFloat(b)
        }

        return inputBuffer
    }

    // 결과 처리 함수
    fun postprocessOutput(outputBuffer: FloatArray): Int {
        val predictedClass = outputBuffer.indices.maxByOrNull { outputBuffer[it] } ?: -1
        return predictedClass
    }

    fun runInference(inputBuffer: ByteBuffer, outputBuffer: Array<FloatArray>) {
        interpreter.run(inputBuffer, outputBuffer)
    }

    fun runFaceRatingModel(bitmap: Bitmap) {
        try {
            val inputBuffer = preprocessImage(bitmap, imageSize)
            runInference(inputBuffer, outputBuffer)
            val predictedScore = postprocessOutput(outputBuffer[0])

            val faceScoreView = findViewById<TextView>(R.id.checkMyScoreByAI)
            faceScoreView.text = "당신의 AI 외모 점수는 ${predictedScore}점 이에요!"
        } catch (e: Exception) {
            e.printStackTrace()
            val errorMessage = when (e) {
                is IllegalArgumentException -> "모델 입력 또는 출력 형식이 잘못되었습니다."
                is IllegalStateException -> "모델 실행 중 오류가 발생했습니다."
                else -> "알 수 없는 오류가 발생했습니다."
            }
            Toast.makeText(this, "모델 실행 중 오류 발생: $errorMessage", Toast.LENGTH_SHORT).show()
        }
    }
}