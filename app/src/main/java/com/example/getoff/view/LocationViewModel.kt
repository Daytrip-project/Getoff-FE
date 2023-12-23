package com.example.getoff.view

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.getoff.BuildConfig
import com.example.getoff.api.BusRouteApiService
import com.example.getoff.api.BusRouteSeoulApiService
import com.example.getoff.config.RetrofitConfig
import com.example.getoff.dto.BusStop
import com.example.getoff.dto.busRouteSeouls
import com.example.getoff.response.BusRouteSeoulLocResponse
import com.example.getoff.response.BusRouteSeoulResponse
import com.example.getoff.response.ThirdResponse
import com.example.getoff.util.CalcUtil
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import java.util.Locale

private const val TAG = "BusSearchFragment_재성"
private const val OPENAPI_SERVICE_KEY = BuildConfig.OPENAPI_KEY;

class LocationViewModel : ViewModel() {

    companion object {
        const val CITY_CODE_SEOUL = 10000000
    }

    private val retrofitKdataBriis = RetrofitConfig.retrofitKdataBriis.create(BusRouteApiService::class.java)
    private val retrofitSeoulBri = RetrofitConfig.retrofitSeoulBri.create(BusRouteSeoulApiService::class.java)


    private val _locationData = MutableLiveData<Pair<Double, Double>>()
    val locationData: LiveData<Pair<Double, Double>>
        get() = _locationData
    fun updateLocation(latitude: Double, longitude: Double) {
        _locationData.value = Pair(latitude, longitude)
    }


    private val _cityCode = MutableLiveData<Int>()
    val cityCode: LiveData<Int>
        get() = _cityCode
    suspend fun requestCityCode(targetCityName: String): Int? {
        val cityCodeResponse = retrofitKdataBriis.getAllCityCode(URLDecoder.decode(OPENAPI_SERVICE_KEY),
            "json")
        var cityList = cityCodeResponse.body()?.response?.body?.items?.item
        if (cityList == null) {
            _cityCode.value = CITY_CODE_SEOUL
        }
        else {
            cityList.let { cities ->
                for (city in cities!!) {
                    if (city.cityname.contains(targetCityName)) {
                        _cityCode.value = city.citycode
                    } else {
                        _cityCode.value = CITY_CODE_SEOUL
                    }
                }
            }
        }
        Log.d(TAG, "onViewCreated: " + _cityCode)

        return _cityCode.value
    }


    private val _routeIdList = MutableLiveData<List<String>>()
    val routeIdList: LiveData<List<String>>
        get() = _routeIdList
    suspend fun requestRouteId(cityCode: Int, busNumber: String): List<String>? {
        var routeIdListResult = mutableListOf<String>()

        val routeIdResponse = retrofitKdataBriis.getRouteId(URLDecoder.decode(OPENAPI_SERVICE_KEY),
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

        val busStopListResponse1 = retrofitKdataBriis.getBusStopList(URLDecoder.decode(OPENAPI_SERVICE_KEY),
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


    private val _seoulBusStopList = MutableLiveData<List<BusRouteSeoulResponse.MsgBody.Item>>()
    val seoulBusStopList: LiveData<List<BusRouteSeoulResponse.MsgBody.Item>>
        get() = _seoulBusStopList
    suspend fun requestSeoulBusStopList(routeId: String): List<BusRouteSeoulResponse.MsgBody.Item>? {
        var busStopListResult = mutableListOf<BusRouteSeoulResponse.MsgBody.Item>()

        val busStopListResponse = retrofitSeoulBri.getBusStopList(URLDecoder.decode(OPENAPI_SERVICE_KEY),
            routeId, "json")

        Log.d(TAG, "onViewCreated: busStopListResponse| " + busStopListResponse.raw().body().toString())

        var busStopLIst = busStopListResponse.body()?.msgBody?.itemList
        for(busStop in busStopLIst!!){
            Log.d(TAG, "onViewCreated: busStopLIst| " + busStop.toString())
            busStopListResult.add(busStop)
        }
        _seoulBusStopList.value = busStopListResult
        Log.d(TAG, "onViewCreated: " + _seoulBusStopList)

        return _seoulBusStopList.value
    }

    private val _seoulBusStopLocList = MutableLiveData<List<BusRouteSeoulLocResponse.MsgBody.Item>>()
    val seoulBusStopLocList: LiveData<List<BusRouteSeoulLocResponse.MsgBody.Item>>
        get() = _seoulBusStopLocList
    suspend fun requestSeoulBusStopLocList(routeId: String): List<BusRouteSeoulLocResponse.MsgBody.Item>? {
        var busStopListLocResult = mutableListOf<BusRouteSeoulLocResponse.MsgBody.Item>()

        val busStopListLocResponse = retrofitSeoulBri.getBusStopLocList(URLDecoder.decode(OPENAPI_SERVICE_KEY),
            routeId, "json")

//        Log.d(TAG, "onViewCreated: busStopListLocResponse| " + busStopListLocResponse.toString())

        var busStopLocLIst = busStopListLocResponse.body()?.msgBody?.itemList
        for(busStopLoc in busStopLocLIst!!){
            Log.d(TAG, "onViewCreated: busStopLocLIst| " + busStopLoc.toString())
            busStopListLocResult.add(busStopLoc)
        }
        _seoulBusStopLocList.value = busStopListLocResult
        Log.d(TAG, "onViewCreated: " + _seoulBusStopLocList)

        return _seoulBusStopLocList.value
    }

    fun getTransportInfoAsync(targetCityName: String, busNumber: String): Deferred<List<List<BusStop>>> {
        return viewModelScope.async {
            val allBusStopList = mutableListOf<List<BusStop>>()
            val busStopBindedList = mutableListOf<BusStop>()

            val cityCode = requestCityCode(targetCityName)
            if (cityCode == CITY_CODE_SEOUL) {
                val route = busRouteSeouls.find { it.name == busNumber }
//                val busStopSeoulLocList = requestSeoulBusStopLocList(route?.routeId.toString())
                val busStopSeoulList = requestSeoulBusStopList(route?.routeId.toString())

                for (i in busStopSeoulList!!.indices) {
                    val busStop = BusStop(busStopSeoulList[i].busRouteId, busStopSeoulList[i].stationNm, targetCityName,
                        busStopSeoulList!![i].gpsX.toDouble(), busStopSeoulList!![i].gpsY.toDouble())
                    busStopBindedList.add(busStop)
                }
                allBusStopList.add(busStopBindedList)
            } else {
                val routeIdList = requestRouteId(cityCode!!, busNumber)
                for (routeId in routeIdList!!) {
                    val busStopList = requestBusStopList(cityCode, routeId)
                    for (i in busStopList!!.indices) {
                        val busStop = BusStop(busStopList[i].routeid, busStopList[i].nodenm, targetCityName,
                            busStopList!![i].gpslati, busStopList!![i].gpslong)
                        busStopBindedList.add(busStop)
                    }
                    allBusStopList.add(busStopBindedList)
                }
            }

            allBusStopList
        }
    }

    fun getUserCityName(context: Context): Deferred<String> {
        return viewModelScope.async {
            var targetCityName: String = "구미"
            val currentLocationData = withContext(Dispatchers.Main) {
                _locationData.value
            }
            currentLocationData?.let { locationData ->
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(locationData.first, locationData.second, 10)
                targetCityName = addresses!![0]?.adminArea.toString()
            }
            targetCityName
        }
    }

    fun getUserStation(context: Context, stationLists: List<List<BusStop>>): Deferred<List<BusStop>> {
        return viewModelScope.async {
            val currentLocationData = withContext(Dispatchers.Main) {
                _locationData.value
            }

            var userStation: BusStop = stationLists[0][0]
            var userStations = mutableListOf<BusStop>()
            currentLocationData?.let { locationData ->
                var minDistance: Double = 100.0
                for (stationList in stationLists) {
                    for (station in stationList) {
                        val distance = CalcUtil.calcDistanceByCoordinate(locationData.first, locationData.second, station.lat, station.lon)
                        if (minDistance > distance) {
                            minDistance = distance
                            userStation = station
                        }
                    }
                    userStations.add(userStation)
                }
            }

            userStations
        }
    }
}