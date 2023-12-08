package com.example.getoff.util

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.example.getoff.R
import com.example.getoff.dto.BusStop
import com.example.getoff.dto.Station
import com.example.getoff.layout.AlarmActivity
import com.example.getoff.layout.BusRouteFragment
import com.example.getoff.layout.MainActivity
import com.example.getoff.view.ShareStationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class GpsUtil : Service() {

    private lateinit var shareStationViewModel: ShareStationViewModel

    private val CHANNEL_ID: String = "getoff_app_notification"
    private val NOTIFICATION_ID: Int = 410

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var destination: Station? = null
    private val observer = Observer<Station> { data ->
        destination = data
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        this.requestLocationPermission()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0 ?: return
                for (location in p0.locations) {
                    val speedKmH = location.speed * 3.6
                    Log.d("SpeedCheck", "Speed: $speedKmH km/h")

                    if (speedKmH > 20) {
                        suggestAlarmNotification()
                    }
                }
            }
        }

        shareStationViewModel = ShareStationViewModel()
        shareStationViewModel.destination.observeForever(observer)
        startLocationUpdates()
    }

//    @SuppressLint("MissingPermission")
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
            fusedLocationClient.requestLocationUpdates(it, locationCallback, null) }
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

    override fun onDestroy() {
        super.onDestroy()
        shareStationViewModel.destination.removeObserver(observer)
    }

//    init {
//        companionContext = context
//    }
//
//    companion object {
//        private var companionContext: Context? = null
//
//        @SuppressLint("MissingPermission")
//        fun getLocation() {
//            val fusedLocationProviderClient =
//                companionContext?.let { LocationServices.getFusedLocationProviderClient(it) }
//
//            fusedLocationProviderClient?.lastLocation
//                ?.addOnSuccessListener { success: Location? ->
//                    success?.let { location ->
////                        return location.latitude.toFloat()
////                        "${location.latitude}, ${location.longitude}"
//                    }
//                }
//                ?.addOnFailureListener { fail ->
//                    fail.localizedMessage
//                }
//        }
//    }

    /** 위치 권한 SDK 버전 29 이상**/
    @RequiresApi(Build.VERSION_CODES.Q)
    private val permissionsLocationUpApi29Impl = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    /** 위치 권한 SDK 버전 29 이하**/
    @TargetApi(Build.VERSION_CODES.P)
    private val permissionsLocationDownApi29Impl = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    /** 위치정보 권한 요청**/
//    fun requestLocationPermission() {
//        if (Build.VERSION.SDK_INT >= 29) {
//            if (ActivityCompat.checkSelfPermission(
//                    context,
//                    permissionsLocationUpApi29Impl[0]
//                ) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(
//                    context,
//                    permissionsLocationUpApi29Impl[1]
//                ) != PackageManager.PERMISSION_GRANTED ||
//                ActivityCompat.checkSelfPermission(
//                    context,
//                    permissionsLocationUpApi29Impl[2]
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    context as Activity,
//                    permissionsLocationUpApi29Impl,
//                    REQUEST_LOCATION
//                )
//            }
//        } else {
//            if (ActivityCompat.checkSelfPermission(
//                    context,
//                    permissionsLocationDownApi29Impl[0]
//                ) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(
//                    context,
//                    permissionsLocationDownApi29Impl[1]
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    context as Activity,
//                    permissionsLocationDownApi29Impl,
//                    REQUEST_LOCATION
//                )
//            }
//        }
//    }

//    override fun onLocationChanged(p0: Location) {
//        val speedKmH = location.speed * 3.6
//
//        // 속도 로깅
//        Log.d("SpeedCheck", "Speed: $speedKmH km/h")
//
//        // 시속 20km 이상 확인
//        if (speedKmH > 20) {
//            Log.d("SpeedCheck", "사용자가 시속 20km 이상으로 이동 중입니다.")
//        }
//    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}