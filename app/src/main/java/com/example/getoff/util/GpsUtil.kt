package com.example.getoff.util

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.activityViewModels
import androidx.work.WorkManager
import com.example.getoff.R
import com.example.getoff.dto.BusStop
import com.example.getoff.layout.AlarmActivity
import com.example.getoff.layout.MainActivity
import com.example.getoff.view.LocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt
import java.util.Calendar
import kotlin.math.pow

class GpsUtil : Service() {

    private val CHANNEL_ID: String = "getoff_app_notification"
    private val SERVICE_NOTIFICATION_ID: Int = 410
    private val ARRIVE_ALARM_NOTIFICATION_ID: Int = 318
    private val SUGGEST_ALARM_NOTIFICATION_ID: Int = 98

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var notificationManager: NotificationManager

    private var isSetDestination: Boolean = false
    private var bus_stop_list = arrayListOf<BusStop>()
    private var destination_longitude: Double = 0.0
    private var destination_latitude: Double = 0.0

    private lateinit var destinationReceiver: BroadcastReceiver
    private lateinit var notificationReceiver: BroadcastReceiver

    var testTrigger = 0

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(SERVICE_NOTIFICATION_ID, createNotification())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0 ?: return
                for (location in p0.locations) {
                    val speedKmH = location.speed * 3.6
                    Log.d("SpeedCheck", "Speed: $speedKmH km/h")

                    if (speedKmH > 20) {
                        notifySuggestAlarm()
                    }

                    val intent = Intent("com.example.UPDATE_LOCATION")
                    intent.putExtra("latitude", location.latitude)
                    intent.putExtra("longitude", location.longitude)
                    sendBroadcast(intent)

//                    val currentStation = updateUserStation(location.latitude, location.longitude)

                    if(isSetDestination &&
                        CalcUtil.calcDistanceByCoordinate(location.latitude, location.longitude, destination_latitude, destination_longitude) * 1000 >= 100){
//                    testTrigger += 5
//                    if(isSetDestination && destination_latitude != 0.0 && destination_longitude != 0.0 && destination_latitude < testTrigger && destination_longitude < testTrigger){
                        val intent = Intent(this@GpsUtil, AlarmActivity::class.java)
                        startActivity(intent)

                        // destination local reset + reset broadcast
                        resetDestination()
                    }
                }
            }
        }

        startLocationUpdates()

        destinationReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onReceive(context: android.content.Context, intent: android.content.Intent) {
                bus_stop_list = intent.getParcelableExtra("bus_stop_list", ArrayList::class.java) as ArrayList<BusStop>
//                bus_stop_list: ArrayList<BusStop>? = intent.getParcelableExtra("bus_stop_list", ArrayList<BusStop>?)
                destination_longitude = intent.getDoubleExtra("destination_longitude", 0.0)
                destination_latitude = intent.getDoubleExtra("destination_latitude", 0.0)
                isSetDestination = true
                manageAlarmNotification()
            }
        }
        notificationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: android.content.Context, intent: android.content.Intent) {
                if (intent.action == "com.example.CANCEL_ARRIVE_ALARM") {
                    resetDestination()
                }
                if (intent.action == "com.example.CANCEL_SUGGEST_ALARM") {
                    notificationManager.cancel(SUGGEST_ALARM_NOTIFICATION_ID)
                }
                if (intent.action == "com.example.CANCEL_GPS_ALARM") {
                    stopForeground(true)
                    WorkManager.getInstance(context).cancelAllWorkByTag("PERIODIC_GPS_WORKER_TAG")
                    stopSelf()
                }
            }
        }
        registerReceiver(destinationReceiver, IntentFilter("com.example.UPDATE_DESTINATION"))
        val filter = IntentFilter().apply {
            addAction("com.example.CANCEL_SUGGEST_ALARM")
            addAction("com.example.CANCEL_ARRIVE_ALARM")
            addAction("com.example.CANCEL_GPS_ALARM")
        }
        registerReceiver(notificationReceiver, filter)
    }

    private fun resetDestination() {
        destination_longitude = 0.0
        destination_latitude = 0.0
        isSetDestination = false
        notificationManager.cancel(ARRIVE_ALARM_NOTIFICATION_ID)
    }

    private fun updateUserStation(currentLat: Double, currentLon: Double) : BusStop? {
        var minDistance: Double = 100.0
        var userStation: BusStop? = null
        for (station in bus_stop_list) {
            val distance = CalcUtil.calcDistanceByCoordinate(currentLat, currentLon, station.lat, station.lon)
            if (minDistance > distance) {
                minDistance = distance
                userStation = station
            }
        }
        return userStation
    }

    private fun startLocationUpdates() {

        val locationRequest = LocationRequest.Builder(1000L)
            .setIntervalMillis(1000L)  // 위치 업데이트 간격 설정 (밀리초 단위)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)  // 위치 요청의 우선순위 설정
            .build()

//        val settingsClient = LocationServices.getSettingsClient(this)
//        val task = settingsClient.checkLocationSettings(locationSettingsRequest)
//
//        task.addOnSuccessListener { locationSettingsResponse ->
//            // 위치 설정이 이미 적절하게 설정되어 있음
//            proceedAfterPermission()
//        }

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
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "getoff_app_service_channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableVibration(false) // 진동 비활성화
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
//        val notificationIntent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            notificationIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )

        val actionIntent = Intent("com.example.CANCEL_GPS_ALARM")

        val actionPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            actionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("서비스 실행 중")
            .setContentText("속도 모니터링이 활성화되었습니다.")
            .setSmallIcon(R.mipmap.ic_launcher_round) // 알림에 표시할 아이콘 설정
            .setContentIntent(actionPendingIntent)
            .setVibrate(null)
            .addAction(R.drawable.cancel_alarm_button, "중지", actionPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        return notificationBuilder.build()
    }

    private fun notifySuggestAlarm() {
        val lastNotificationDate = getLastNotificationDate()
        val today = Calendar.getInstance()

        // 날짜가 변경되었는지 확인
        if (lastNotificationDate.get(Calendar.YEAR) != today.get(Calendar.YEAR) ||
            lastNotificationDate.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR)) {

            // 알림 생성 및 표시
            val notification = createSuggestAlarmNotification()
            notificationManager.notify(SUGGEST_ALARM_NOTIFICATION_ID, notification)

            // 오늘 날짜를 마지막 알림 날짜로 저장
            saveLastNotificationDate(today)
        }
    }

    private fun createSuggestAlarmNotification(): Notification {
//        val notificationIntent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            notificationIntent,
//            Intent.FILL_IN_ACTION or PendingIntent.FLAG_IMMUTABLE
//        )

        val actionIntent = Intent("com.example.CANCEL_SUGGEST_ALARM")

        val actionPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            actionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("하차 알람을 설정하시겠습니까?")
            .setContentText("클릭하여 앱을 열 수 있습니다.")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(actionPendingIntent)
            .setVibrate(longArrayOf(0, 500))
            .addAction(R.drawable.cancel_alarm_button, "취소", actionPendingIntent)
            .build()

//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(SUGGEST_ALARM_NOTIFICATION_ID, notification)
        return notification
    }

    private fun getLastNotificationDate(): Calendar {
        val sharedPreferences = getSharedPreferences("GETOFF_APP_SUGGEST_LOG", Context.MODE_PRIVATE)
        val time = sharedPreferences.getLong("LastNotificationTime", 0)
        val calendar = Calendar.getInstance()
        if (time != 0L) {
            calendar.timeInMillis = time
        }
        return calendar
    }

    private fun saveLastNotificationDate(calendar: Calendar) {
        val sharedPreferences = getSharedPreferences("GETOFF_APP_SUGGEST_LOG", Context.MODE_PRIVATE)
        with (sharedPreferences.edit()) {
            putLong("LastNotificationTime", calendar.timeInMillis)
            apply()
        }
    }

    private fun manageAlarmNotification() {
        val actionIntent = Intent("com.example.CANCEL_ARRIVE_ALARM")

        val actionPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            actionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("하차 알람을 취소하시겠습니까?")
            .setContentText("클릭하여 앱을 열 수 있습니다.")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(actionPendingIntent)
            .setVibrate(longArrayOf(0, 500))
            .addAction(R.drawable.cancel_alarm_button, "알람취소", actionPendingIntent) // 버튼 추가
            .build()

//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(ARRIVE_ALARM_NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onDestroy() {
        super.onDestroy()
        this.unregisterReceiver(destinationReceiver)
        this.unregisterReceiver(notificationReceiver)
    }

}