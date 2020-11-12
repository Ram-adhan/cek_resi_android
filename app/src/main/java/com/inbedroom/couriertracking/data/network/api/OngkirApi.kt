package com.inbedroom.couriertracking.data.network.api

import com.inbedroom.couriertracking.data.entity.CityEntity
import com.inbedroom.couriertracking.data.entity.CostRequest
import com.inbedroom.couriertracking.data.entity.OngkirResult
import com.inbedroom.couriertracking.data.entity.SubDistrict
import com.inbedroom.couriertracking.data.network.response.RajaOngkirResponse
import retrofit2.Response
import retrofit2.http.*

interface OngkirApi {

    @GET
    @Headers("RajaOngkir: true")
    suspend fun getCityList(@Url url: String):
            Response<RajaOngkirResponse<List<CityEntity>>>

    @GET
    @Headers("RajaOngkir: true")
    suspend fun getSubDistrictList(@Url url: String, @Query("city") value: String):
            Response<RajaOngkirResponse<List<SubDistrict>>>

    @POST
    @Headers("RajaOngkir: true")
    suspend fun getCalculation(@Url url: String, @Body body:CostRequest):
            Response<RajaOngkirResponse<List<OngkirResult>>>
}