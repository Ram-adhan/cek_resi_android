package com.inbedroom.couriertracking.data.entity

data class Tracking(
    val date: String,
    val desc: String,
    var location: String? = null
)