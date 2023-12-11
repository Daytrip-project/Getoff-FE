package com.example.getoff.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Timer
import java.util.TimerTask

class ShareGPSViewModel : ViewModel() {
    private val _locationData = MutableLiveData<Pair<Double, Double>>()
    val locationData: LiveData<Pair<Double, Double>> = _locationData

    fun updateLocation(longitude: Double, latitude: Double) {
        _locationData.value = Pair(longitude, latitude)
    }

//    private val _arriveTrigger = MutableLiveData<Boolean>(false)
//    val arriveTrigger: LiveData<Boolean> = _arriveTrigger
//    fun updateArriveTrigger() {
//        Thread.sleep(7000)
//        _arriveTrigger.value = true
//    }
}