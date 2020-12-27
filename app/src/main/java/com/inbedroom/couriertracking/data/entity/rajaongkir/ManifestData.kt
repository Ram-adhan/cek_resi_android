package com.inbedroom.couriertracking.data.entity.rajaongkir

import com.google.gson.annotations.SerializedName
import com.inbedroom.couriertracking.data.entity.Tracking

data class ManifestData (
    @SerializedName("manifest_description")
    val description: String,

    @SerializedName("manifest_date")
    val date: String,

    @SerializedName("manifest_time")
    val time: String
){
    fun toTracking() = Tracking(
        "$date $time:00",
        description
    )
}