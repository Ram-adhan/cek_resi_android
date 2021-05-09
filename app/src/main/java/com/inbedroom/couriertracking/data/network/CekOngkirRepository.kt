package com.inbedroom.couriertracking.data.network

import com.inbedroom.couriertracking.data.entity.*
import com.inbedroom.couriertracking.data.network.response.DataResult

interface CekOngkirRepository {
    suspend fun getCityList(forceUpdate: Boolean = false):
            DataResult<List<AddressEntity>>

    suspend fun getTariffList(request: CostRequest):
            DataResult<List<OngkirResult>>

    suspend fun getSubdistrict(cityId: String):
            DataResult<List<AddressEntity>>

    suspend fun getAddressList(): DataResult<List<AddressEntity>>

    suspend fun getLocationList(param: String):
            DataResult<List<SimpleLocation>>
}