package com.inbedroom.couriertracking.data.network

import com.inbedroom.couriertracking.data.entity.TrackDataEntity
import com.inbedroom.couriertracking.data.entity.rajaongkir.TrackResult
import com.inbedroom.couriertracking.data.network.api.TrackApi
import com.inbedroom.couriertracking.data.network.response.BaseResponse
import com.inbedroom.couriertracking.data.network.response.DataResult
import com.inbedroom.couriertracking.data.network.response.RajaOngkirResponse
import com.inbedroom.couriertracking.utils.ServiceData
import com.inbedroom.couriertracking.utils.handleApiError
import com.inbedroom.couriertracking.utils.handleApiSuccess
import javax.inject.Inject

class TrackingRemoteRepositoryImpl @Inject constructor(
    private val trackingApi: TrackApi
) : TrackingRemoteRepository {

    override suspend fun retrieveTrackingNew(
        awb: String,
        courier: String
    ): DataResult<BaseResponse<TrackDataEntity>> {
        return try {
            val response = trackingApi.getTrackingNew(ServiceData.API_KEY, courier, awb)
            if (response.isSuccessful){
                handleApiSuccess(response.body()!!)
            }else{
                DataResult.Error(Exception("Tracking Server Maintenance"))
            }
        }catch (e: Exception){
            DataResult.Error(e)
        }
    }

    override suspend fun retrieveRajaOngkir(
        awb: String,
        courier: String
    ): DataResult<RajaOngkirResponse<TrackResult>> {
        val url = StringBuilder().append(ServiceData.RAJAONGKIR_URL).append("/waybill").toString()
        return try {
            val response = trackingApi.getRajaongkirTrack(url, awb, courier)
            if (response.isSuccessful){
                handleApiSuccess(response.body()!!)
            }else{
                DataResult.Error(exception = Exception("Tracking Server Maintenance"))
            }
        }catch (e: Exception){
            DataResult.Error(e)
        }
    }
}