package com.daytrip.getoff.dto


import com.google.gson.annotations.SerializedName

data class SecondResponse(
    @SerializedName("response")
    val response: Response
) {
    data class Response(
        @SerializedName("body")
        val body: Body,
        @SerializedName("header")
        val header: Header
    ) {
        data class Body(
            @SerializedName("items")
            val items: Items,
            @SerializedName("numOfRows")
            val numOfRows: Int,
            @SerializedName("pageNo")
            val pageNo: Int,
            @SerializedName("totalCount")
            val totalCount: Int
        ) {
            data class Items(
                @SerializedName("item")
                val item: List<Item>
            ) {
                data class Item(
                    @SerializedName("endnodenm")
                    val endnodenm: String,
                    @SerializedName("endvehicletime")
                    val endvehicletime: Int,
                    @SerializedName("routeid")
                    val routeid: String,
                    @SerializedName("routeno")
                    val routeno: Int,
                    @SerializedName("routetp")
                    val routetp: String,
                    @SerializedName("startnodenm")
                    val startnodenm: String,
                    @SerializedName("startvehicletime")
                    val startvehicletime: String
                )
            }
        }

        data class Header(
            @SerializedName("resultCode")
            val resultCode: String,
            @SerializedName("resultMsg")
            val resultMsg: String
        )
    }
}