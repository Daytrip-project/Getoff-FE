package com.example.getoff.api

import com.example.getoff.dao.BoardingPointReceive
import com.example.getoff.dao.BusRouteReceive
import com.example.getoff.dto.BusStop
import com.example.getoff.dto.User
import retrofit2.http.GET
import retrofit2.http.Query

interface BusStopService {
    /**
     * @brief "API - GET BUS ROUTE LIST"
     */
    @GET("route")
    suspend fun getBusRoute(
        @Query("busNumber") busNumber: String,
    ): BusRouteReceive

    /**
     * @brief "API - GET/CHECK/UPDATE CURRENT BUS STOP"
     */
    @GET("point")
    suspend fun getBoardingPoint(
        @Query("busNumber") busNumber: String,
        @Query("lat") lat: Float,
        @Query("lon") lon: Float
    ): BoardingPointReceive
}

//    /**
//     * @brief "API - POST Login Access"
//     */
//    @FormUrlEncoded
//    @POST("api/users/edit")
//    Call<ModelUserCreate> doPostUserCreate(
//    @Field("first_name") String first_name
//    , @Field("last_name") String last_name
//    , @Field("name") String name
//    );
//}