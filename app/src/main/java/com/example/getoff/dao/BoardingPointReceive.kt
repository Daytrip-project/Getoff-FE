package com.example.getoff.dao

import com.example.getoff.dto.BusStop
import com.google.gson.annotations.SerializedName

class BoardingPointReceive {
    @SerializedName("boardingPoint")
    var boardingPoint: BusStop? = null

    override fun toString(): String {
        return """BoardingInfo {
            boardingPoint="$boardingPoint"
        }
        """
    }
}