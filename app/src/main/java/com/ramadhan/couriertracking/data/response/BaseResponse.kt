package com.ramadhan.couriertracking.data.response

data class BaseResponse<T>(
    val result: Boolean,
    val data: T?,
    val message: String
)