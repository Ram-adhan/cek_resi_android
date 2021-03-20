package com.inbedroom.couriertracking.utils

import com.inbedroom.couriertracking.data.network.response.*
import retrofit2.Response

fun <T: Any> handleApiError(response: Response<T>): DataResult.Error{
    val error = ApiErrorHandler().parseError(response)
    return DataResult.Error(Exception(error.message), error.status)
}

fun <T: Any> handleApiSuccess(response: BaseResponse<T>): DataResult.Success<BaseResponse<T>>{
    return DataResult.Success(response)
}

fun <T: Any> handleApiSuccess(response: DataListOnlyResponse<T>): DataResult.Success<T>{
    return DataResult.Success(response.data)
}

fun <T: Any> handleApiSuccess(response: RajaOngkirBaseResponse<T>): DataResult.Success<T>{
    return DataResult.Success(response.results)
}

fun <T: Any> handleApiSuccess(response: T): DataResult.Success<T>{
    return DataResult.Success(response)
}