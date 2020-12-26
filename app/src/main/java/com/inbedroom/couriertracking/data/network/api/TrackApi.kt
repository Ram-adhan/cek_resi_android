package com.inbedroom.couriertracking.data.network.api

import com.inbedroom.couriertracking.data.entity.TrackData
import com.inbedroom.couriertracking.data.entity.rajaongkir.TrackResult
import com.inbedroom.couriertracking.data.network.response.BaseResponse
import com.inbedroom.couriertracking.data.network.response.RajaOngkirResponse
import retrofit2.Response
import retrofit2.http.*

interface TrackApi {

    @GET("track")
    suspend fun getTrackingNew(
        @Query("api_key") api_key: String,
        @Query("courier") courier: String,
        @Query("awb") awb: String
    ): Response<BaseResponse<TrackData>>

    @POST
    @Headers("Rajaongkir: true")
    suspend fun getRajaongkirTrack(
        @Url url: String,
        @Field("waybill") waybill: String,
        @Field("courier") courier: String
    ): Response<RajaOngkirResponse<TrackResult>>
}