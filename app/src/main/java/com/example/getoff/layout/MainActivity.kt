package com.example.getoff.layout

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.getoff.R
import com.example.getoff.worker.SensorWorker
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val REQUEST_LOCATION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (requestLocationPermission()) {
            proceedAfterPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // 권한이 부여되었을 때 실행할 코드
                proceedAfterPermission()
            } else {
                finishAffinity()
            }
        }
    }

    private fun proceedAfterPermission() {
        // 첫 실행 확인
//        val prefs = getSharedPreferences("daytrip_getoff_app_local_context_registry", Context.MODE_PRIVATE)
//        if (!prefs.getBoolean("app_execute", false)) {
            scheduleSensorWorker()
//            prefs.edit().putBoolean("app_execute", true).apply()
//        }

        val newFragment: Fragment = BusSearchFragment()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainContainer, newFragment)
        transaction.commit()
    }

    private fun scheduleSensorWorker() {
        val workRequest = PeriodicWorkRequestBuilder<SensorWorker>(1, TimeUnit.MINUTES)
            .build()

//        WorkManager.getInstance(this).enqueue(workRequest)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sensor_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

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
    private fun requestLocationPermission() : Boolean {
        var isAlreadyPermitted: Boolean = false

        if (Build.VERSION.SDK_INT >= 29) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permissionsLocationUpApi29Impl[0]
                ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                    this,
                    permissionsLocationUpApi29Impl[1]
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    permissionsLocationUpApi29Impl[2]
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this as Activity,
                    permissionsLocationUpApi29Impl,
                    REQUEST_LOCATION
                )
            }
            else {
                isAlreadyPermitted = true
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permissionsLocationDownApi29Impl[0]
                ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                    this,
                    permissionsLocationDownApi29Impl[1]
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this as Activity,
                    permissionsLocationDownApi29Impl,
                    REQUEST_LOCATION
                )
            }
            else {
                isAlreadyPermitted = true
            }
        }

        return isAlreadyPermitted
    }
}