package com.inbedroom.couriertracking.utils

import com.inbedroom.couriertracking.data.network.response.ApiErrorHandler
import com.inbedroom.couriertracking.data.network.response.BaseResponse
import com.inbedroom.couriertracking.data.network.response.DataResult
import com.inbedroom.couriertracking.data.network.response.RajaOngkirBaseResponse
import retrofit2.Response

fun <T: Any> handleApiError(response: Response<T>): DataResult.Error{
    val error = ApiErrorHandler().parseError(response)
    return DataResult.Error(Exception(error.message), error.status)
}

fun <T: Any> handleApiSuccess(response: BaseResponse<T>): DataResult.Success<BaseResponse<T>>{
    return DataResult.Success(response)
}

fun <T: Any> handleApiSuccess(response: RajaOngkirBaseResponse<T>): DataResult.Success<RajaOngkirBaseResponse<T>>{
    return DataResult.Success(response)
}