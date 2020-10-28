package com.inbedroom.couriertracking.data.network.response

data class RajaOngkirBaseResponse<T> (
    val status: RajaOngkirStatus,
    val results: T
)