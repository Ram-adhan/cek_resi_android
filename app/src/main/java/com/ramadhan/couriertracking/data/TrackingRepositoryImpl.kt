package com.ramadhan.couriertracking.data

import com.ramadhan.couriertracking.data.response.BaseResponse
import com.ramadhan.couriertracking.data.response.OperationCallback
import com.ramadhan.couriertracking.data.response.entity.Track
import com.ramadhan.couriertracking.data.response.entity.Tracking
import com.ramadhan.couriertracking.data.rest.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackingRepositoryImpl : TrackingRepository {
    private var call: Call<BaseResponse<Track<List<Tracking>>>>? = null

    override fun retrieveTrackingData(
        awb: String,
        courier: String,
        callback: OperationCallback<BaseResponse<Track<List<Tracking>>>>
    ) {
        call = ApiClient.build().getTracking(awb = awb, courier = courier)

        call?.enqueue(object : Callback<BaseResponse<Track<List<Tracking>>>> {
            override fun onFailure(call: Call<BaseResponse<Track<List<Tracking>>>>, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(
                call: Call<BaseResponse<Track<List<Tracking>>>>,
                response: Response<BaseResponse<Track<List<Tracking>>>>
            ) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body())
                } else {
                    callback.onError(response.body()?.message)
                }
            }

        })
    }


    override fun cancel() {
        call?.cancel()
    }
}