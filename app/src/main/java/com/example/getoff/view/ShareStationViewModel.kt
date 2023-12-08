package com.example.getoff.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.getoff.dto.Station

class ShareStationViewModel : ViewModel() {
    private val _eventData = MutableLiveData<Station>()
    val destination: LiveData<Station> get() = _eventData

    fun setDestination(station: Station) {
        _eventData.value = station
    }
}