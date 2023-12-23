package com.daytrip.getoff.api

import com.daytrip.getoff.dto.FirstResponse
import com.daytrip.getoff.dto.SecondResponse
import com.daytrip.getoff.response.ThirdResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BusRouteApiService {
    @GET("getCtyCodeList")
    suspend fun getAllCityCode(@Query("serviceKey") serviceKey: String,
                        @Query("_type") _type: String
    ): Response<FirstResponse>

    @GET("getRouteNoList")
    suspend fun getRouteId(@Query("serviceKey") serviceKey: String,
                   @Query("numOfRows") numOfRows: Int,
                   @Query("pageNo") pageNo: Int,
                   @Query("_type") _type: String,
                   @Query("cityCode") cityCode: Int,
                   @Query("routeNo") routeNo: String
    ): Response<SecondResponse>

    @GET("getRouteAcctoThrghSttnList")
    suspend fun getBusStopList(@Query("serviceKey") serviceKey: String,
                       @Query("numOfRows") numOfRows: Int,
                       @Query("pageNo") pageNo: Int,
                       @Query("_type") _type: String,
                       @Query("cityCode") cityCode: Int,
                       @Query("routeId") routeId: String
    ): Response<ThirdResponse>

}