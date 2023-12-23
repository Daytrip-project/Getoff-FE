package com.daytrip.getoff.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.daytrip.getoff.worker.SensorWorker.Companion.scheduleSensorWorker

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            scheduleSensorWorker(context)
        }
    }
}