package com.inbedroom.couriertracking.data.entity

import com.google.gson.annotations.SerializedName

data class TrackDataEntity(
    @SerializedName("summary")
    val summary: Summary,
    @SerializedName("detail")
    val info: Information,
    @SerializedName("history")
    val track: List<Tracking>
)