package com.ramadhan.couriertracking.utils

import com.ramadhan.couriertracking.data.network.response.ApiErrorHandler
import com.ramadhan.couriertracking.data.network.response.BaseResponse
import com.ramadhan.couriertracking.data.network.response.DataResult
import retrofit2.Response

fun <T: Any> handleApiError(response: Response<T>): DataResult.Error{
    val error = ApiErrorHandler().parseError(response)
    return DataResult.Error(Exception(error.message), error.status)
}

fun <T: Any> handleApiSuccess(response: BaseResponse<T>): DataResult.Success<BaseResponse<T>>{
    return DataResult.Success(response)
}