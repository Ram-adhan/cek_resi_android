package com.inbedroom.couriertracking.data.network.api

import com.inbedroom.couriertracking.data.entity.Courier
import com.inbedroom.couriertracking.data.entity.CourierVersion
import com.inbedroom.couriertracking.data.network.response.DataListOnlyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface CouriersApi {

    @GET
    suspend fun getCouriers(@Url url: String):
            Response<List<Courier>>
}