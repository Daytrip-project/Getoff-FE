package com.example.getoff.dao

import com.example.getoff.dto.BusStop
import com.google.gson.annotations.SerializedName

class BusRouteReceive {
    @SerializedName("busRoute")
    var busRoute: List<BusStop>? = null

    override fun toString(): String {
        return """BoardingInfo {
            busRoute="$busRoute"
        }
        """
    }
}