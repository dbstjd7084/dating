package com.dbsthd2459.datingapp.auth

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dbsthd2459.datingapp.MainActivity
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.dbsthd2459.datingapp.utils.FirebaseRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class JoinActivity : AppCompatActivity() {

    private val TAG = "JoinActivity"

    private lateinit var auth: FirebaseAuth

    private var nickname = ""
    private var gender = ""
    private var city = ""
    private var age = ""
    private var uid = ""

    lateinit var profileImage : ImageView
    var imageUploaded = false // 프로필 이미지 등록 여부

    var uploading = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        auth = Firebase.auth

        profileImage = findViewById(R.id.imageArea)

        val getAction = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                // 이미지가 선택되었을 때
                profileImage.setImageURI(uri)
                imageUploaded = true
            } else {
                // 이미지 선택이 취소된 경우 (uri가 null)
                Toast.makeText(this, "이미지 선택이 취소되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        profileImage.setOnClickListener {
            getAction.launch("image/*")
        }


        val joinBtn = findViewById<Button>(R.id.joinBtn)
        joinBtn.setOnClickListener {

            val email = findViewById<TextInputEditText>(R.id.emailArea)
            val pwd = findViewById<TextInputEditText>(R.id.pwdArea)

            val emailCheck = email.text.toString()

            // 이메일 작성 확인
            if (emailCheck.isBlank()) {
                Toast.makeText(this, "이메일을 작성해주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!emailCheck.contains("@") ||
                !emailCheck.contains(".")) {
                Toast.makeText(this, "올바른 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 비밀번호 작성 확인
            if (pwd.text.toString().isBlank() ||
                pwd.text.toString().length < 6) {
                Toast.makeText(this, "비밀번호를 6자 이상 작성해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // pwd와 pwdCheck 둘 다 같은지
            if (pwd.text.toString() != findViewById<TextInputEditText>(R.id.pwdCheckArea).text.toString()) {
                Toast.makeText(this, "입력한 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 성별 확인
            val radioGender = findViewById<RadioGroup>(R.id.radioGroupGender)
            if (radioGender.checkedRadioButtonId == -1) {
                Toast.makeText(this, "성별을 선택해주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val checked = findViewById<RadioButton>(radioGender.checkedRadioButtonId).text.toString()
                if (checked == "남자") {
                    gender = "M"
                } else gender = "W"
            }

            // 프로필 이미지 확인
            if (!imageUploaded) {
                Toast.makeText(this, "프로필 이미지를 등록해주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (uploading) {
                Toast.makeText(this, "현재 회원가입 진행중 입니다..", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            city = findViewById<TextInputEditText>(R.id.cityArea).text.toString()
            age = findViewById<TextInputEditText>(R.id.ageArea).text.toString()
            nickname = findViewById<TextInputEditText>(R.id.nicknameArea).text.toString()

            // 지역 확인
            if (city.isBlank()) {
                Toast.makeText(this, "지역을 작성해주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 나이 확인
            if (age.isBlank() ||
                age.toIntOrNull() == null ||
                age.toShort() < 1 ||
                age.toShort() > 200) {
                Toast.makeText(this, "나이를 작성하지 않았거나,\n올바른 값이 아닙니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 닉네임 확인
            if (nickname.isBlank()) {
                Toast.makeText(this, "닉네임을 작성해주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email.text.toString(), pwd.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val user = auth.currentUser
                        uid = user?.uid.toString()

                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    val errorMessage = task.exception?.localizedMessage ?: "회원가입에 실패했습니다."
                                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                                    return@OnCompleteListener
                                }

                                // Get new FCM registration token
                                val token = task.result

                                val userModel = UserDataModel(
                                    uid,
                                    FirebaseAuthUtils.getEmail(),
                                    nickname,
                                    age,
                                    gender,
                                    city,
                                    token.toString()
                                )

                                FirebaseRef.userInfoRef.child(uid).setValue(userModel)

                                uploading = true
                                uploadImage(uid)

                        })

                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    }
                }

        }

    }

    // 가입 시 프로필 이미지 등록
    private fun uploadImage(uid : String) {

        val storage = Firebase.storage
        val storageRef = storage.reference.child("$uid.png")

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
            uploading = false
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
}