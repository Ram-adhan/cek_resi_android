package com.inbedroom.couriertracking.data.room

import com.inbedroom.couriertracking.data.entity.AddressEntity
import com.inbedroom.couriertracking.data.entity.CityEntity

interface AddressRepository {
    suspend fun addData(data: AddressEntity)
    suspend fun addAllData(list: List<AddressEntity>)
    suspend fun removeAllData()
    suspend fun getAllData(): List<AddressEntity>
    fun saveCityList(list: List<CityEntity>)
    fun getCityList(): List<CityEntity>
}