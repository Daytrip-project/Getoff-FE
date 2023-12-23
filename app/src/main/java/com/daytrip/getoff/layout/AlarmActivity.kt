package com.daytrip.getoff.layout

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Vibrator
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.getoff.R

class AlarmActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 화면 깨우기 및 잠금 화면 위에 표시
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)

        setContentView(R.layout.activity_alarm)

        val frameLayout = findViewById<FrameLayout>(R.id.effect_area)
        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink_animation)
        frameLayout.startAnimation(blinkAnimation)

        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(0, 500, 1000)
        vibrator.vibrate(pattern, 1)

        val buttonStopAlarm = findViewById<Button>(R.id.alarmStop)
        buttonStopAlarm.setOnClickListener {
            vibrator.cancel()
            finishAffinity()
        }
    }
}
