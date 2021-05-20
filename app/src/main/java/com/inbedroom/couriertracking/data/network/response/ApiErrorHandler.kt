package com.inbedroom.couriertracking.data.network.response

import com.google.gson.GsonBuilder
import retrofit2.Response
import java.io.IOException

class ApiErrorHandler {
    fun parseError(response: Response<*>): ApiError {
        val gson = GsonBuilder().create()

        return try {
            gson.fromJson(
                response.errorBody()?.string(),
                ApiError::class.java
            ) ?: ApiError(response.code(), "Something went wrong")
        } catch (e: IOException) {
            return ApiError()
        }
    }
}

data class ApiError(
    val status: Int,
    var message: String
) {
    constructor() : this(0, "")
}