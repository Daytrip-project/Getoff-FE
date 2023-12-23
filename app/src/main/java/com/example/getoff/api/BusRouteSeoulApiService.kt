package com.example.getoff.api

import com.example.getoff.dto.FirstResponse
import com.example.getoff.dto.SecondResponse
import com.example.getoff.response.BusRouteSeoulLocResponse
import com.example.getoff.response.BusRouteSeoulResponse
import com.example.getoff.response.ThirdResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BusRouteSeoulApiService {

    @GET("getBusRouteList")
    suspend fun getBusStopList(@Query("serviceKey") serviceKey: String,
                               @Query("stdt") stdt: String,
                               @Query("resultType") resultType: String,
    ): Response<String>//Response<BusRouteSeoulResponse>

    @GET("getRoutePath")
    suspend fun getBusStopLocList(@Query("ServiceKey") ServiceKey: String,
                               @Query("busRouteId") busRouteId: String,
                               @Query("resultType") resultType: String,
    ): Response<BusRouteSeoulLocResponse>
}