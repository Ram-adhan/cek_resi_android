package com.inbedroom.couriertracking.data.network

import com.inbedroom.couriertracking.data.entity.Courier
import com.inbedroom.couriertracking.data.entity.CourierVersion
import com.inbedroom.couriertracking.data.network.response.DataListOnlyResponse
import com.inbedroom.couriertracking.data.network.response.DataResult

interface CourierRepository {
    suspend fun getCouriers():
            DataResult<List<Courier>>

    suspend fun getVersion():
            DataResult<CourierVersion>
}