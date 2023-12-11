package com.example.getoff.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.getoff.api.BusRouteApiService
import com.example.getoff.config.RetrofitConfig
import com.example.getoff.response.ThirdResponse
import com.google.android.gms.tasks.Tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLDecoder

private const val TAG = "BusSearchFragment_재성"
private const val OPENAPI_SERVICE_KEY = "weNHZ6HW3Vdw42cnm3ZEksjMmrr5ds22NPev8aDPCt3QsbMuFfH68tZzn%2BoHJkAZfHX17%2Fe5jdctHTMwAiX6NQ%3D%3D"

class LocationViewModel : ViewModel() {

    private val service = RetrofitConfig.retrofit.create(BusRouteApiService::class.java)


    private val _locationData = MutableLiveData<Pair<Double, Double>>()
    val locationData: LiveData<Pair<Double, Double>>
        get() = _locationData
    fun updateLocation(longitude: Double, latitude: Double) {
        _locationData.value = Pair(longitude, latitude)
    }


    private val _cityCode = MutableLiveData<Int>()
    val cityCode: LiveData<Int>
        get() = _cityCode
    suspend fun requestCityCode(targetCityName: String): Int? {
        val cityCodeResponse = service.getAllCityCode(URLDecoder.decode(OPENAPI_SERVICE_KEY),
            "json")
        var cityList = cityCodeResponse.body()?.response?.body?.items?.item
        cityList.let { cities ->
            for(city in cities!!){
                if(city.cityname.take(2) == targetCityName) {
                    _cityCode.value = city.citycode
                }
            }
        }
        Log.d(TAG, "onViewCreated: " + _cityCode)

        return _cityCode.value
    }


    private val _routeIdList = MutableLiveData<List<String>>()
    val routeIdList: LiveData<List<String>>
        get() = _routeIdList
    suspend fun requestRouteId(cityCode: Int, busNumber: Int): List<String>? {
        var routeIdListResult = mutableListOf<String>()

        val routeIdResponse = service.getRouteId(URLDecoder.decode(OPENAPI_SERVICE_KEY),
            10, 1,"json", cityCode, busNumber)
        var routeIdItems = routeIdResponse.body()?.response?.body?.items?.item
        for(routeId in routeIdItems!!){
            routeIdListResult.add(routeId.routeid)
        }
        _routeIdList.value = routeIdListResult

        return _routeIdList.value
    }


    private val _busStopList = MutableLiveData<List<ThirdResponse.Response.Body.Items.Item>>()
    val busStopList: LiveData<List<ThirdResponse.Response.Body.Items.Item>>
        get() = _busStopList
    suspend fun requestBusStopList(cityCode: Int, routeId: String): List<ThirdResponse.Response.Body.Items.Item>? {
        var busStopListResult = mutableListOf<ThirdResponse.Response.Body.Items.Item>()

        val busStopListResponse1 = service.getBusStopList(URLDecoder.decode(OPENAPI_SERVICE_KEY),
            100, 1,"json", cityCode, routeId)

        var busStopLIst = busStopListResponse1.body()?.response?.body?.items?.item
        for(busStop in busStopLIst!!){
            Log.d(TAG, "onViewCreated: busStopLIst| " + busStop.toString())
            busStopListResult.add(busStop)
        }
        _busStopList.value = busStopListResult
        Log.d(TAG, "onViewCreated: " + _busStopList)

        return _busStopList.value
    }

    fun getTransportInfoAsync(targetCityName: String, busNumber: Int): Deferred<List<List<ThirdResponse.Response.Body.Items.Item>>> {
        return viewModelScope.async {
            val cityCode = requestCityCode(targetCityName)
            val routeIdList = requestRouteId(cityCode!!, busNumber)
            val allBusStopList = mutableListOf<List<ThirdResponse.Response.Body.Items.Item>>()
            for (routeId in routeIdList!!) {
                allBusStopList.add(requestBusStopList(cityCode, routeId)!!)
            }
            allBusStopList
        }
    }

    fun getUserCityName(): Deferred<String> {
        return viewModelScope.async {
            var targetCityName: String = "구미"
            val currentLocationData = withContext(Dispatchers.Main) {
                _locationData.value
            }
//            currentLocationData?.let { locationData ->
//                targetCityName = "구미"
//            }
            targetCityName
        }
    }
}