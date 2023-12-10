package com.example.getoff.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShareGPSViewModel : ViewModel() {
    private val _locationData = MutableLiveData<Pair<Double, Double>>()
    val locationData: LiveData<Pair<Double, Double>> = _locationData

    fun updateLocation(longitude: Double, latitude: Double) {
        _locationData.value = Pair(longitude, latitude)
    }
}