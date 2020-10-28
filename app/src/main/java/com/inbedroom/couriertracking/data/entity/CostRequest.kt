package com.inbedroom.couriertracking.data.entity

data class CostRequest (
    val origin: String,
    val destination: String,
    val weight: Int,
    val courier: String
)