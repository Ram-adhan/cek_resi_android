package com.inbedroom.couriertracking.data.room

import com.inbedroom.couriertracking.data.entity.AddressEntity
import com.inbedroom.couriertracking.data.entity.CityEntity

interface AddressRepository {
    suspend fun addData(data: AddressEntity)
    suspend fun addAllData(list: List<AddressEntity>)
    suspend fun removeAllData()
    suspend fun getAllData(): List<AddressEntity>
    suspend fun removeAllCity()
    fun saveCityList(list: List<CityEntity>)
    suspend fun getCityList(): List<AddressEntity>
    suspend fun getDistrictFromCity(cityId: String): List<AddressEntity>
}