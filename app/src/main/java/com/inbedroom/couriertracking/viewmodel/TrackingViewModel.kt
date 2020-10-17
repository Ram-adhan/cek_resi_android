package com.inbedroom.couriertracking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inbedroom.couriertracking.data.entity.Courier
import com.inbedroom.couriertracking.data.entity.HistoryEntity
import com.inbedroom.couriertracking.data.entity.TrackData
import com.inbedroom.couriertracking.data.network.TrackingRemoteRepository
import com.inbedroom.couriertracking.data.network.response.BaseResponse
import com.inbedroom.couriertracking.data.network.response.DataResult
import com.inbedroom.couriertracking.data.room.HistoryRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrackingViewModel @Inject constructor(
    private val remoteRepository: TrackingRemoteRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _trackingData = MutableLiveData<TrackData>()
    val trackingData: LiveData<TrackData> = _trackingData

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private val _onMessageError = MutableLiveData<String>()
    val onMessageError: LiveData<String> = _onMessageError

    fun saveAsHistory(awb: String, courier: Courier) {
        viewModelScope.launch {
            historyRepository.addHistory(HistoryEntity(awb, courier))
        }
    }

    fun getTrackingData(awb: String, courier: String) {
        _isViewLoading.postValue(true)
        viewModelScope.launch {
            val result: DataResult<BaseResponse<TrackData>> = remoteRepository.retrieveTrackingNew(awb, courier)
            when (result) {
                is DataResult.Success -> {
                    _trackingData.value = result.data?.data
                }
                is DataResult.Error -> {
                    _onMessageError.postValue(result.errorMessage)
                }
            }
            _isViewLoading.postValue(false)
        }
    }
}