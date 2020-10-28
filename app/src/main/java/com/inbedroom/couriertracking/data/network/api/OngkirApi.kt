package com.inbedroom.couriertracking.data.network.api

import com.inbedroom.couriertracking.data.entity.CityEntity
import com.inbedroom.couriertracking.data.entity.CostRequest
import com.inbedroom.couriertracking.data.entity.OngkirResult
import com.inbedroom.couriertracking.data.network.response.RajaOngkirBaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface OngkirApi {

    @GET
    fun getCityList(@Url url: String):
            Response<RajaOngkirBaseResponse<List<CityEntity>>>

    @POST
    fun getCalculation(@Url url: String, @Body body:CostRequest):
            Response<RajaOngkirBaseResponse<OngkirResult>>
}