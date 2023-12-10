package com.example.getoff.util

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.getoff.R
import com.example.getoff.layout.AlarmActivity
import com.example.getoff.layout.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class GpsUtil : Service() {

    private val CHANNEL_ID: String = "getoff_app_notification"
    private val NOTIFICATION_ID: Int = 410

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0 ?: return
                for (location in p0.locations) {
                    val speedKmH = location.speed * 3.6
                    Log.d("SpeedCheck", "Speed: $speedKmH km/h")

                    if (speedKmH > 20) {
                        suggestAlarmNotification()
                    }

                    val intent = Intent("com.example.UPDATE_LOCATION")
                    intent.putExtra("longitude", location.longitude)
                    intent.putExtra("latitude", location.latitude)
                    sendBroadcast(intent)
                }
            }
        }

        startLocationUpdates()
    }

    private fun startLocationUpdates() {

        val locationRequest = LocationRequest.Builder(1000L)
            .setIntervalMillis(1000L)  // 위치 업데이트 간격 설정 (밀리초 단위)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)  // 위치 요청의 우선순위 설정
            .build()

        locationRequest?.let { if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
            fusedLocationClient.requestLocationUpdates(it, locationCallback, null)
        }
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
            .setContentText("속도 모니터링이 활성화되었습니다.")
            .setSmallIcon(R.mipmap.ic_launcher_round) // 알림에 표시할 아이콘 설정
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        return notificationBuilder.build()
    }


    private fun suggestAlarmNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            Intent.FILL_IN_ACTION or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("하차 알람을 설정하시겠습니까?")
            .setContentText("클릭하여 앱을 열 수 있습니다.")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun arrivedAlarmNotification() {
        val intent = Intent(this, AlarmActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}