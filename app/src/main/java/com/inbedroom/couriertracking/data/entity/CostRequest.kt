package com.inbedroom.couriertracking.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CostRequest (
    var origin: String,
    var originType: String,
    var destination: String,
    var destinationType: String,
    var weight: Int,
    var courier: String = ""
): Parcelable{
    constructor(): this("", "", "", "", 0, "")
}