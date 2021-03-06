package com.inbedroom.couriertracking.data.network.api

import com.inbedroom.couriertracking.data.entity.TrackData
import com.inbedroom.couriertracking.data.network.response.BaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApi {

    @GET("track")
    suspend fun getTrackingNew(
        @Query("api_key") api_key: String,
        @Query("courier") courier: String,
        @Query("awb") awb: String
    ): Response<BaseResponse<TrackData>>
}