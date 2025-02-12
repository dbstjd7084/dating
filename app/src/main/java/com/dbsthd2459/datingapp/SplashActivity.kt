package com.dbsthd2459.datingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.dbsthd2459.datingapp.auth.IntroActivity
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val uid = FirebaseAuthUtils.getUid()

        if (uid == "null") {

            Handler().postDelayed({
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) // 액티비티 전환 애니메이션 제거
                startActivity(intent)
                finish()
            }, 2000)

        } else {

            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)

        }
    }
}