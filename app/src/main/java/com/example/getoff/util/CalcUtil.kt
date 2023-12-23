package com.example.getoff.util

import kotlin.math.pow

class CalcUtil {
    companion object {
        fun calcDistanceByCoordinate(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val R = 6371.0 // 지구의 반지름 (단위: km)

            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val rLat1 = Math.toRadians(lat1)
            val rLat2 = Math.toRadians(lat2)

            val a = Math.sin(dLat / 2).pow(2) +
                    Math.sin(dLon / 2).pow(2) * Math.cos(rLat1) * Math.cos(rLat2)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

            return R * c // 거리 반환 (단위: km)
        }
    }
}