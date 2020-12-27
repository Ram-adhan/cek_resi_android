package com.inbedroom.couriertracking.data.entity


data class TrackData(
    val summary: Summary,
    val info: Information,
    val track: List<Tracking>
)