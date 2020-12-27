package com.inbedroom.couriertracking.data.network

import com.inbedroom.couriertracking.data.network.response.BaseResponse
import com.inbedroom.couriertracking.data.entity.TrackDataEntity
import com.inbedroom.couriertracking.data.entity.rajaongkir.TrackResult
import com.inbedroom.couriertracking.data.network.response.DataResult
import com.inbedroom.couriertracking.data.network.response.RajaOngkirResponse

interface TrackingRemoteRepository {

    suspend fun retrieveTrackingNew(
        awb: String,
        courier: String
    ): DataResult<BaseResponse<TrackDataEntity>>

    suspend fun retrieveRajaOngkir(
        awb: String,
        courier: String
    ): DataResult<RajaOngkirResponse<TrackResult>>
}