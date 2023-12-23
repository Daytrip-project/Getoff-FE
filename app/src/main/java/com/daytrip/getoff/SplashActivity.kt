package com.daytrip.getoff

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.daytrip.getoff.layout.MainActivity

class SplashActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.daytrip.getoff.R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed( {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finish()
        }, 5000)
    }
}