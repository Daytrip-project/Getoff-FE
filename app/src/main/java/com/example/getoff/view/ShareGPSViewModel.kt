package com.example.getoff.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.getoff.response.ThirdResponse
import kotlinx.coroutines.launch

private const val TAG = "ShareGPSViewModel_재성"
class ShareGPSViewModel : ViewModel() {
    private val _locationData = MutableLiveData<Pair<Double, Double>>()
    val locationData: LiveData<Pair<Double, Double>> = _locationData

    fun updateLocation(longitude: Double, latitude: Double) {
        _locationData.value = Pair(longitude, latitude)
    }


    private val _cityCoderesult = MutableLiveData<Int>()
    val cityCoderesult: LiveData<Int>
        get() = _cityCoderesult
    fun savecityCoderesult(cityCode: Int){
        viewModelScope.launch {
            _cityCoderesult.value = cityCode
        }
    }

    private var _routeIdListresult = MutableLiveData<List<String>>()
    val routeIdListresult: LiveData<List<String>>
        get() = _routeIdListresult

    fun saverouteIdList(list: List<String>){
        viewModelScope.launch {
            _routeIdListresult.value = list
        }
    }

    private var _busStopListresult = MutableLiveData<List<ThirdResponse.Response.Body.Items.Item>>()
    val busStopListresult: LiveData<List<ThirdResponse.Response.Body.Items.Item>>
        get() = _busStopListresult

    fun savebusStopLlist(list: List<ThirdResponse.Response.Body.Items.Item>){
        viewModelScope.launch {
            _busStopListresult.value = list
        }
    }
}