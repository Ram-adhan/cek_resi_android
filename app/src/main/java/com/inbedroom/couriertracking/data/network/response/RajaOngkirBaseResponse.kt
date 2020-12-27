package com.inbedroom.couriertracking.data.network.response

import com.google.gson.annotations.SerializedName

data class RajaOngkirBaseResponse<T> (
    @SerializedName("status")
    val status: RajaOngkirStatus,

    @SerializedName("results")
    val results: T,

    @SerializedName("result")
    val result: T
)