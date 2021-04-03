package com.inbedroom.couriertracking.data.network.response

import com.google.gson.annotations.SerializedName


data class DataListOnlyResponse<T> (

    @SerializedName("version")
    val version: Int?,

    @SerializedName("version_code")
    val versionCode: String?,

    val data: T?
)