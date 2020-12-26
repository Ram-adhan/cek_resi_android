package com.inbedroom.couriertracking.data.entity.rajaongkir

import com.google.gson.annotations.SerializedName

data class SummaryTrack (
    @SerializedName("courier_name")
    val courierName: String,

    @SerializedName("waybill_number")
    val waybill: String,

    @SerializedName("service_code")
    val service: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("shipper_name")
    val shipper: String,

    @SerializedName("origin")
    val origin: String,

    @SerializedName("receiver_name")
    val receiver: String,

    @SerializedName("destination")
    val destination: String

)