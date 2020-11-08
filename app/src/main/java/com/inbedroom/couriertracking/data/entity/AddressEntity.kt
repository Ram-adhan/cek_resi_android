package com.inbedroom.couriertracking.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "address")
data class AddressEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val type: String,
    val addressId: String,
    val postalCode: String? = null

)