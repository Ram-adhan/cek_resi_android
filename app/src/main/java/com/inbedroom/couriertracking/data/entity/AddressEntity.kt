package com.inbedroom.couriertracking.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = AddressEntity.tableName)
data class AddressEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val type: String,
    val addressId: String,
    val cityId: String,
    val postalCode: String? = null

){
    companion object{
        const val tableName = "address"
    }
}