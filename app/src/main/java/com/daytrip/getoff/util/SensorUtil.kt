package com.daytrip.getoff.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.daytrip.getoff.layout.MainActivity
import com.example.getoff.R

class SensorUtil : Service(),  SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometerSensor: Sensor? = null

    private val CHANNEL_ID: String = "getoff_app_notification"
    private val NOTIFICATION_ID: Int = 410

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.d(null, "accel sensored")
        if (event?.sensor == accelerometerSensor) {
            event?.let {
                // 가속도 센서 값 처리
                val x = it.values[0]
                val y = it.values[1]
                val z = it.values[2]

                // 특정 조건 검사
                if (isVehicleMotion(x, y, z)) {
//                    startForeground(NOTIFICATION_ID, sendNotification())
                    sendNotification()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun isVehicleMotion(x: Float, y: Float, z: Float): Boolean {
        val accelerationThreshold = 5f // m/s²
//        val accelerationThreshold = 0
        val magnitude = Math.sqrt((x * x + y * y + z * z).toDouble())
        Log.d(null, "${magnitude > accelerationThreshold}, ${magnitude}")
        return magnitude > accelerationThreshold
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "getoff_app_service_channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("서비스 실행 중")
            .setContentText("가속도 센서 모니터링이 활성화되었습니다.")
            .setSmallIcon(R.mipmap.ic_launcher_round) // 알림에 표시할 아이콘 설정
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        return notificationBuilder.build()
    }


    private fun sendNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            Intent.FILL_IN_ACTION or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("앱을 실행하시겠습니까?")
            .setContentText("클릭하여 앱을 열 수 있습니다.")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}