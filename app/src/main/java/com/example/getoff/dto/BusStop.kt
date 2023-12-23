package com.example.getoff.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BusStop(val routeId: String, val name: String, val locationName: String, val lon: Double, val lat: Double) : Station,
    Parcelable