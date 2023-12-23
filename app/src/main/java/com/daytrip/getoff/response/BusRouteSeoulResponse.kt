package com.daytrip.getoff.response


import com.google.gson.annotations.SerializedName

data class BusRouteSeoulResponse(
    @SerializedName("comMsgHeader")
    val comMsgHeader: ComMsgHeader,
    @SerializedName("msgBody")
    val msgBody: MsgBody,
    @SerializedName("msgHeader")
    val msgHeader: MsgHeader
) {
    data class ComMsgHeader(
        @SerializedName("errMsg")
        val errMsg: Any,
        @SerializedName("requestMsgID")
        val requestMsgID: Any,
        @SerializedName("responseMsgID")
        val responseMsgID: Any,
        @SerializedName("responseTime")
        val responseTime: Any,
        @SerializedName("returnCode")
        val returnCode: Any,
        @SerializedName("successYN")
        val successYN: Any
    )

    data class MsgBody(
        @SerializedName("itemList")
        val itemList: List<Item>
    ) {
        data class Item(
            @SerializedName("arsId")
            val arsId: String,
            @SerializedName("beginTm")
            val beginTm: String,
            @SerializedName("busRouteAbrv")
            val busRouteAbrv: String,
            @SerializedName("busRouteId")
            val busRouteId: String,
            @SerializedName("busRouteNm")
            val busRouteNm: String,
            @SerializedName("direction")
            val direction: String,
            @SerializedName("fullSectDist")
            val fullSectDist: String,
            @SerializedName("gpsX")
            val gpsX: String,
            @SerializedName("gpsY")
            val gpsY: String,
            @SerializedName("lastTm")
            val lastTm: String,
            @SerializedName("posX")
            val posX: String,
            @SerializedName("posY")
            val posY: String,
            @SerializedName("routeType")
            val routeType: String,
            @SerializedName("sectSpd")
            val sectSpd: String,
            @SerializedName("section")
            val section: String,
            @SerializedName("seq")
            val seq: String,
            @SerializedName("station")
            val station: String,
            @SerializedName("stationNm")
            val stationNm: String,
            @SerializedName("stationNo")
            val stationNo: String,
            @SerializedName("transYn")
            val transYn: String,
            @SerializedName("trnstnid")
            val trnstnid: String
        )
    }

    data class MsgHeader(
        @SerializedName("headerCd")
        val headerCd: String,
        @SerializedName("headerMsg")
        val headerMsg: String,
        @SerializedName("itemCount")
        val itemCount: Int
    )
}