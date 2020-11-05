package com.inbedroom.couriertracking.data.entity

data class Ongkir(
    var courier: String,
    var service: String,
    var etd: String,
    var cost: Int
){
    constructor(): this("", "", "", 0)
}