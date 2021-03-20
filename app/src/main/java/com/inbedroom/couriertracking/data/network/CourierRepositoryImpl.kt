package com.inbedroom.couriertracking.data.network

import com.inbedroom.couriertracking.data.PreferencesManager
import com.inbedroom.couriertracking.data.entity.Courier
import com.inbedroom.couriertracking.data.entity.CourierVersion
import com.inbedroom.couriertracking.data.network.api.CouriersApi
import com.inbedroom.couriertracking.data.network.response.DataResult
import com.inbedroom.couriertracking.utils.ServiceData
import com.inbedroom.couriertracking.utils.handleApiError
import com.inbedroom.couriertracking.utils.handleApiSuccess
import java.lang.Exception
import java.lang.StringBuilder
import javax.inject.Inject

class CourierRepositoryImpl @Inject constructor(
    private val couriersApi: CouriersApi,
    private val preferencesManager: PreferencesManager
) : CourierRepository {

    private val baseUrl = ServiceData.COURIERS_URL

    private var currentVersion: Int = 0
    private var currentVersionCode: String = ""

    override suspend fun getCouriers(): DataResult<List<Courier>> {
        val url = StringBuilder().append(baseUrl).append("/couriers")
        val versionUrl = StringBuilder().append(baseUrl).append("/version")

        if (currentVersion == 0) {
            val version = preferencesManager.getVersion()
            currentVersion = 0
            currentVersionCode = "0"
        }

        val couriersAsset = preferencesManager.readCourierAsset()

        return try {
            val versionResponse = couriersApi.getVersion(versionUrl.toString())
            if (versionResponse.isSuccessful) {
                val lastVersion = versionResponse.body()?.data!!
                if (lastVersion.version > currentVersion) {
                    preferencesManager.saveLatestVersion(lastVersion)
                    val couriers = couriersApi.getCouriers(url.toString())

                    if (couriers.isSuccessful){
                        handleApiSuccess(couriers.body()!!)
                    }else{
                        DataResult.Success(couriersAsset)
                    }
                }else{
                    DataResult.Success(couriersAsset)
                }
            }else{
                DataResult.Success(couriersAsset)
            }
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun getVersion(): DataResult<CourierVersion> {
        val url = StringBuilder().append(baseUrl).append("/version")

        if (currentVersion == 0) {
            val version = preferencesManager.getVersion()
            currentVersion = version.version
            currentVersionCode = version.version_code
        }

        return try {
            val response = couriersApi.getVersion(url.toString())
            if (response.isSuccessful) {
                val currentVersion = response.body()?.data!!
                if (currentVersion.version > this.currentVersion) {
                    handleApiSuccess(currentVersion)
                } else {
                    handleApiSuccess(CourierVersion(this.currentVersion, currentVersionCode))
                }
            } else {
                handleApiError(response)
            }
        } catch (e: Exception) {
            DataResult.Error(exception = e)
        }
    }


}