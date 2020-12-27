package com.inbedroom.couriertracking.data.network

import com.inbedroom.couriertracking.data.PreferencesManager
import com.inbedroom.couriertracking.data.entity.*
import com.inbedroom.couriertracking.data.network.api.OngkirApi
import com.inbedroom.couriertracking.data.network.response.DataResult
import com.inbedroom.couriertracking.data.room.AddressRepository
import com.inbedroom.couriertracking.utils.ServiceData
import com.inbedroom.couriertracking.utils.handleApiError
import com.inbedroom.couriertracking.utils.handleApiSuccess
import javax.inject.Inject
import kotlin.collections.ArrayList

class CekOngkirRepositoryImpl @Inject constructor(
    private val ongkirApi: OngkirApi,
    private val addressRepository: AddressRepository,
    private val preferencesManager: PreferencesManager
) : CekOngkirRepository {

    private val baseUrl = ServiceData.RAJAONGKIR_URL
    private val cityList: MutableList<AddressEntity> = ArrayList()

    override suspend fun getCityList(forceUpdate: Boolean): DataResult<List<AddressEntity>> {
        val url = StringBuilder().append(baseUrl).append("/city")
        return try {
            if (forceUpdate) {
                getFromNetwork(url.toString())
            } else if (cityList.isNullOrEmpty()) {
                val fromDB = addressRepository.getCityList()
                if (!fromDB.isNullOrEmpty()) {
                    cityList.clear()
                    cityList.addAll(fromDB)
                    handleApiSuccess(cityList)
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

    private suspend fun getFromNetwork(url: String): DataResult<List<AddressEntity>> {
        val response = ongkirApi.getCityList(url)
        addressRepository.removeAllData()
        return if (response.isSuccessful) {
            val tempData = response.body()?.rajaongkir?.results
            if (tempData != null) {
                cityList.clear()
                tempData.forEach {
                    cityList.add(it.toAddressEntity())
                    addressRepository.addData(it.toAddressEntity())
                }
                handleApiSuccess(cityList)
            } else {
                DataResult.Empty
            }
        } else {
            handleApiError(response)
        }
    }

    override suspend fun getTariffList(
        request: CostRequest
    ): DataResult<List<OngkirResult>> {
        val url = StringBuilder().append(baseUrl).append("/cost")
        return try {
            val response = ongkirApi.getCalculation(
                url.toString(),
                request
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

    override suspend fun getSubdistrict(
        cityId: String
    ): DataResult<List<AddressEntity>> {
        return try {
            val fromDB = addressRepository.getDistrictFromCity(cityId)
            if (fromDB.isNullOrEmpty()) {
                getSubdistrictFromNetwork(cityId)
            } else {
                handleApiSuccess(fromDB)
            }
        } catch (e: Exception) {
            DataResult.Error(Exception("Internal Error"))
        }
    }

    private suspend fun getSubdistrictFromNetwork(cityId: String): DataResult<List<AddressEntity>> {
        val url = StringBuilder().append(baseUrl).append("/subdistrict")
        return try {
            val response = ongkirApi.getSubDistrictList(url.toString(), cityId)
            if (response.isSuccessful) {
                if (response.body()!!.rajaongkir.results.isNullOrEmpty()) {
                    DataResult.Empty
                } else {
                    val temp: MutableList<SubDistrict> =
                        response.body()!!.rajaongkir.results as MutableList<SubDistrict>
                    temp.forEach {
                        addressRepository.addData(it.toAddressEntity())
                    }
                    handleApiSuccess(addressRepository.getDistrictFromCity(cityId))
                }
            } else {
                handleApiError(response)
            }
        } catch (e: Exception) {
            DataResult.Error(Exception("Internal Error"))
        }
    }

    override suspend fun getAddressList(): DataResult<List<AddressEntity>> {
        return handleApiSuccess(addressRepository.getAllData())
    }

}