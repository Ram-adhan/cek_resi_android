package com.inbedroom.couriertracking.data.entity

import com.google.gson.annotations.SerializedName

data class SimpleLocation(
    val name: String,

    val type: String,

    @SerializedName("original_id")
    val id: String
)
