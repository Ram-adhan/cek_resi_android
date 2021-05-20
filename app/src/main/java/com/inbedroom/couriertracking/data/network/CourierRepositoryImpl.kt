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

    private val baseUrl = ServiceData.LOCAL_URL

    override suspend fun getCouriers(): DataResult<List<Courier>> {
        val url = StringBuilder().append(baseUrl).append("/couriers/")

        return try {
            val couriers = couriersApi.getCouriers(url.toString())
            if (couriers.isSuccessful){
                val data = couriers.body()!!
                preferencesManager.saveCourierList(data)
            }
            val couriersAsset = preferencesManager.readCourierList()
            DataResult.Success(couriersAsset)
        } catch (e: Exception) {
            val couriersAsset = preferencesManager.readCourierList()
            DataResult.Success(couriersAsset)
        }
    }


}