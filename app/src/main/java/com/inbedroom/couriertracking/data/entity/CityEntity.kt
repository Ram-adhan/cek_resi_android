package com.inbedroom.couriertracking.data.entity

import com.google.gson.annotations.SerializedName

data class CityEntity(
    @SerializedName("city_id")
    val cityId: String,
    @SerializedName("province_id")
    val provinceId: String,
    @SerializedName("province")
    val province: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("city_name")
    val cityName: String,
    @SerializedName("postal_code")
    val postalCode: String
) {
    fun toAddressEntity() =
        AddressEntity(
            name = cityName,
            type = type,
            isCity = true,
            addressId = cityId,
            cityId = cityId,
            postalCode = postalCode
        )
}
