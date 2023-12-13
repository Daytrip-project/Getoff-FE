package com.example.getoff.worker

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.getoff.util.GpsUtil
import com.example.getoff.util.SensorUtil
import java.util.concurrent.TimeUnit

class SensorWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {

        val serviceIntent = Intent(applicationContext, GpsUtil::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.startForegroundService(serviceIntent)
        } else {
            applicationContext.startService(serviceIntent)
        }

        return Result.success()
    }

    companion object {
        fun scheduleSensorWorker(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<SensorWorker>(10, TimeUnit.SECONDS)
                .addTag("PERIODIC_GPS_WORKER_TAG")
                .build()

//        WorkManager.getInstance(this).enqueue(workRequest)
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "sensor_worker",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
