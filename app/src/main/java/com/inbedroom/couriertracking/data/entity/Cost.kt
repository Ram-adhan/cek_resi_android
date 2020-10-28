package com.inbedroom.couriertracking.data.entity

data class Cost(
    val cost: List<CostDetail>,
    val description: String,
    val service: String
)