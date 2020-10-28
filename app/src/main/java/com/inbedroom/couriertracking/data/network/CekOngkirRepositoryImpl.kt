package com.inbedroom.couriertracking.data.network

import com.inbedroom.couriertracking.data.entity.CityEntity
import com.inbedroom.couriertracking.data.entity.CostRequest
import com.inbedroom.couriertracking.data.entity.OngkirResult
import com.inbedroom.couriertracking.data.network.api.OngkirApi
import com.inbedroom.couriertracking.data.network.response.DataResult
import com.inbedroom.couriertracking.data.network.response.RajaOngkirBaseResponse
import com.inbedroom.couriertracking.utils.ServiceData
import com.inbedroom.couriertracking.utils.handleApiError
import com.inbedroom.couriertracking.utils.handleApiSuccess
import java.lang.Exception
import java.lang.StringBuilder
import javax.inject.Inject

class CekOngkirRepositoryImpl @Inject constructor(
    private val ongkirApi: OngkirApi
) : CekOngkirRepository {

    val BaseUrl = ServiceData.ONGKIR_URL

    override suspend fun getCityList(): DataResult<RajaOngkirBaseResponse<List<CityEntity>>> {
        val url = StringBuilder().append(BaseUrl).append("/city")
        return try {
            val response = ongkirApi.getCityList(url.toString())
            if (response.isSuccessful) {
                handleApiSuccess(response.body()!!)
            } else {
                handleApiError(response)
            }
        } catch (e: Exception) {
            DataResult.Error(e)
        }

    }

    override suspend fun getTariffList(
        origin: String,
        destination: String,
        weight: Int,
        courier: String
    ): DataResult<RajaOngkirBaseResponse<OngkirResult>> {
        val url = StringBuilder().append(BaseUrl).append("/cost")
        return try {
            val response = ongkirApi.getCalculation(
                url.toString(),
                CostRequest(origin, destination, weight, courier)
            )
            if (response.isSuccessful){
                handleApiSuccess(response.body()!!)
            }else{
                handleApiError(response)
            }
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

}