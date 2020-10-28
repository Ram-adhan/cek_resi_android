package com.inbedroom.couriertracking.data.network

import com.inbedroom.couriertracking.data.entity.CityEntity
import com.inbedroom.couriertracking.data.entity.OngkirResult
import com.inbedroom.couriertracking.data.network.response.DataResult
import com.inbedroom.couriertracking.data.network.response.RajaOngkirBaseResponse

interface CekOngkirRepository {
    suspend fun getCityList():
            DataResult<RajaOngkirBaseResponse<List<CityEntity>>>

    suspend fun getTariffList(origin: String, destination: String, weight: Int, courier: String):
            DataResult<RajaOngkirBaseResponse<OngkirResult>>
}