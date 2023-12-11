package com.example.getoff.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ThirdResponse(
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
                @Parcelize
                data class Item(
                    @SerializedName("gpslati")
                    val gpslati: Double,
                    @SerializedName("gpslong")
                    val gpslong: Double,
                    @SerializedName("nodeid")
                    val nodeid: String,
                    @SerializedName("nodenm")
                    val nodenm: String,
                    @SerializedName("nodeno")
                    val nodeno: Int,
                    @SerializedName("nodeord")
                    val nodeord: Int,
                    @SerializedName("routeid")
                    val routeid: String
                ) : Parcelable
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