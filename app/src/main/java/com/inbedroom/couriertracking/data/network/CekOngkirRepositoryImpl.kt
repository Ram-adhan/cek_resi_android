package com.inbedroom.couriertracking.data.network

import com.inbedroom.couriertracking.data.PreferencesManager
import com.inbedroom.couriertracking.data.entity.CityEntity
import com.inbedroom.couriertracking.data.entity.CostRequest
import com.inbedroom.couriertracking.data.entity.OngkirResult
import com.inbedroom.couriertracking.data.entity.SubDistrict
import com.inbedroom.couriertracking.data.network.api.OngkirApi
import com.inbedroom.couriertracking.data.network.response.DataResult
import com.inbedroom.couriertracking.utils.ServiceData
import com.inbedroom.couriertracking.utils.handleApiError
import com.inbedroom.couriertracking.utils.handleApiSuccess
import java.util.*
import javax.inject.Inject

class CekOngkirRepositoryImpl @Inject constructor(
    private val ongkirApi: OngkirApi,
    private val preferencesManager: PreferencesManager
) : CekOngkirRepository {

    private val baseUrl = ServiceData.ONGKIR_URL
    private val cityList: MutableList<CityEntity> = ArrayList()

    override suspend fun getCityList(forceUpdate: Boolean): DataResult<List<CityEntity>> {
        val url = StringBuilder().append(baseUrl).append("/city")
        return try {
            if (forceUpdate) {
                getFromNetwork(url.toString())
            } else if (cityList.isNullOrEmpty()) {
                val fromPreferences = preferencesManager.getSavedCityList()
                if (!fromPreferences.isNullOrEmpty()) {
                    cityList.addAll(fromPreferences)
                    handleApiSuccess(preferencesManager.getSavedCityList())
                } else {
                    getFromNetwork(url.toString())
                }
            } else {
                handleApiSuccess(cityList)
            }
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    private suspend fun getFromNetwork(url: String): DataResult<List<CityEntity>> {
        val response = ongkirApi.getCityList(url)
        return if (response.isSuccessful) {
            cityList.addAll(response.body()!!.rajaongkir.results)
            preferencesManager.saveCityList(cityList)
            handleApiSuccess(response.body()!!.rajaongkir)
        } else {
            handleApiError(response)
        }
    }

    override suspend fun getTariffList(
        origin: String,
        destination: String,
        weight: Int,
        courier: String
    ): DataResult<List<OngkirResult>> {
        val url = StringBuilder().append(baseUrl).append("/cost")
        return try {
            val response = ongkirApi.getCalculation(
                url.toString(),
                CostRequest(origin, destination, weight, courier)
            )
            if (response.isSuccessful) {
                handleApiSuccess(response.body()!!.rajaongkir)
            } else {
                handleApiError(response)
            }
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun getSubdistrict(forceUpdate: Boolean): DataResult<List<SubDistrict>> {
        val url = java.lang.StringBuilder().append(baseUrl).append("/subdistrict")
        return DataResult.Empty
    }

}