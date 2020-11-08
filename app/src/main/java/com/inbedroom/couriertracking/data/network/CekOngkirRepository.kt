package com.inbedroom.couriertracking.data.network

import com.inbedroom.couriertracking.data.entity.CityEntity
import com.inbedroom.couriertracking.data.entity.OngkirResult
import com.inbedroom.couriertracking.data.entity.SubDistrict
import com.inbedroom.couriertracking.data.network.response.DataResult

interface CekOngkirRepository {
    suspend fun getCityList(forceUpdate: Boolean = false):
            DataResult<List<CityEntity>>

    suspend fun getTariffList(origin: String, destination: String, weight: Int, courier: String):
            DataResult<List<OngkirResult>>

    suspend fun getSubdistrict(forceUpdate: Boolean = false):
            DataResult<List<SubDistrict>>
}