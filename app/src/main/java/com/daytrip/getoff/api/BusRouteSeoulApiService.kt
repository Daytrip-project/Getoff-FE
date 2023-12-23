package com.daytrip.getoff.api

import com.daytrip.getoff.response.BusRouteSeoulLocResponse
import com.daytrip.getoff.response.BusRouteSeoulResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BusRouteSeoulApiService {

    @GET("getStaionByRoute")
    suspend fun getBusStopList(@Query("serviceKey") serviceKey: String,
                               @Query("busRouteId") busRouteId: String,
                               @Query("resultType") resultType: String,
    ): Response<BusRouteSeoulResponse>

    @GET("getRoutePath")
    suspend fun getBusStopLocList(@Query("ServiceKey") ServiceKey: String,
                               @Query("busRouteId") busRouteId: String,
                               @Query("resultType") resultType: String,
    ): Response<BusRouteSeoulLocResponse>
}