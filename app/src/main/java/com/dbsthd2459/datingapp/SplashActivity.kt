package com.dbsthd2459.datingapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dbsthd2459.datingapp.auth.IntroActivity
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private val TAG = "SplashActivity"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val uid = FirebaseAuthUtils.getUid()

        Log.d(TAG, uid)

        if (uid == "null") {

            Handler().postDelayed({
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            }, 2000)

        } else {

            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            }, 2000)

        }
    }
}