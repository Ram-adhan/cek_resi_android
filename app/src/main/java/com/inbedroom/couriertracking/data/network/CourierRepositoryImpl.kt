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
            val version = preferencesManager.getSavedVersion()
            currentVersion = version.version
            currentVersionCode = version.version_code
        }

        return try {
            val versionResponse = couriersApi.getVersion(versionUrl.toString())
            if (versionResponse.isSuccessful) {
                val lastVersion = versionResponse.body()?.data!!
                if (lastVersion.version > currentVersion) {
                    val couriers = couriersApi.getCouriers(url.toString())
                    if (couriers.isSuccessful){
                        preferencesManager.saveLatestVersion(lastVersion)
                        preferencesManager.saveCourierList(couriers.body()?.data!!)
                    }
                }
            }
            val couriersAsset = preferencesManager.readCourierList()
            DataResult.Success(couriersAsset)
        } catch (e: Exception) {
            val couriersAsset = preferencesManager.readCourierList()
            DataResult.Success(couriersAsset)
        }
    }


}