package com.ramadhan.couriertracking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ramadhan.couriertracking.data.TrackingRepository
import com.ramadhan.couriertracking.data.response.BaseResponse
import com.ramadhan.couriertracking.data.response.OperationCallback
import com.ramadhan.couriertracking.data.response.entity.Track
import com.ramadhan.couriertracking.data.response.entity.Tracking

class TrackingViewModel(private val repository: TrackingRepository) : ViewModel() {

    private val _trackingData = MutableLiveData<Track<List<Tracking>>>()
    val trackingTrack: LiveData<Track<List<Tracking>>> = _trackingData

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private val _onMessageError = MutableLiveData<Any>()
    val onMessageError: LiveData<Any> = _onMessageError

    private val _isNoData = MutableLiveData<Boolean>()
    val isNoData: LiveData<Boolean> = _isNoData

    fun getTrackingData(awb: String, courier: String) {
        _isViewLoading.postValue(true)

        repository.retrieveTrackingData(
            awb,
            courier,
            object : OperationCallback<BaseResponse<Track<List<Tracking>>>> {
                override fun onError(error: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(error)
                }

                override fun onSuccess(data: BaseResponse<Track<List<Tracking>>>?) {
                    _isViewLoading.postValue(false)

                    if (data != null) {
                        if (data.result) {
                            _trackingData.postValue(data.data)
                        } else {
                            _isNoData.postValue(true)
                        }
                    } else {
                        _isNoData.postValue(true)
                    }
                }


            })
    }
}