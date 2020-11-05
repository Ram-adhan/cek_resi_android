package com.inbedroom.couriertracking.data.network.response

import com.google.gson.annotations.SerializedName

data class RajaOngkirStatus (
    @SerializedName("code")
    val code: Int,

    @SerializedName("description")
    val description: String
)