package com.inbedroom.couriertracking.data.entity

data class CostRequest (
    var origin: String,
    var destination: String,
    var weight: Int,
    var courier: String
){
    constructor(): this("", "", 0, "")
}