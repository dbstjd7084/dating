package com.dbsthd2459.datingapp.auth

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dbsthd2459.datingapp.MainActivity
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.utils.FirebaseRef
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        auth = Firebase.auth

        profileImage = findViewById<ImageView>(R.id.imageArea)

        val getAction = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                profileImage.setImageURI(uri)
            }
        )

        profileImage.setOnClickListener {
            getAction.launch("image/*")
        }


        val joinBtn = findViewById<Button>(R.id.joinBtn)
        joinBtn.setOnClickListener {

            val email = findViewById<TextInputEditText>(R.id.emailArea)
            val pwd = findViewById<TextInputEditText>(R.id.pwdArea)

            val emailCheck = email.text.toString()

            // 이메일 작성했는지
            // 비밀번호 작성했는지
            /*if (emailCheck.isEmpty()) {
                Toast.makeText(this, "비어있음", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "채워져있음", Toast.LENGTH_SHORT).show()
            }*/

            // pwd와 pwdCheck 둘 다 같은지

            gender = findViewById<TextInputEditText>(R.id.genderArea).text.toString()
            city = findViewById<TextInputEditText>(R.id.cityArea).text.toString()
            age = findViewById<TextInputEditText>(R.id.ageArea).text.toString()
            nickname = findViewById<TextInputEditText>(R.id.nicknameArea).text.toString()

            auth.createUserWithEmailAndPassword(email.text.toString(), pwd.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val user = auth.currentUser
                        uid = user?.uid.toString()

                        val userModel = UserDataModel(
                            uid,
                            nickname,
                            age,
                            gender,
                            city
                        )

                        FirebaseRef.userInfoRef.child(uid).setValue(userModel)

                        uploadImage(uid)

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    }
                }

        }

    }

    private fun uploadImage(uid : String) {

        val storage = Firebase.storage
        val storageRef = storage.reference.child(uid + ".png")

        profileImage.isDrawingCacheEnabled = true
        profileImage.buildDrawingCache()
        val bitmap = (profileImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }
}