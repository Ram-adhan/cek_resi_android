package com.inbedroom.couriertracking.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CostRequest (
    var origin: String,
    var destination: String,
    var weight: Int,
    var courier: String = ""
): Parcelable{
    constructor(): this("", "", 0, "")
}