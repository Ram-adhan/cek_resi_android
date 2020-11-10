package com.inbedroom.couriertracking.data.entity

import com.google.gson.annotations.SerializedName

data class SubDistrict (
    @SerializedName("subdistrict_id")
    val id: String,

    @SerializedName("city_id")
    val cityId: String,

    @SerializedName("city")
    val city: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("subdistrict_name")
    val name: String
){
    fun toAddressEntity() =
        AddressEntity(
            name = name,
            type = type,
            isCity = false,
            addressId = id,
            cityId = cityId
        )
}