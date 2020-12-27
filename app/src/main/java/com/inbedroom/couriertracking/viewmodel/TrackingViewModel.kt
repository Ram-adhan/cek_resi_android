package com.inbedroom.couriertracking.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inbedroom.couriertracking.data.entity.*
import com.inbedroom.couriertracking.data.entity.rajaongkir.TrackResult
import com.inbedroom.couriertracking.data.network.TrackingRemoteRepository
import com.inbedroom.couriertracking.data.network.response.BaseResponse
import com.inbedroom.couriertracking.data.network.response.DataResult
import com.inbedroom.couriertracking.data.network.response.RajaOngkirResponse
import com.inbedroom.couriertracking.data.room.HistoryRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrackingViewModel @Inject constructor(
    private val remoteRepository: TrackingRemoteRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _trackingData = MutableLiveData<TrackDataEntity>()
    val trackingDataEntity: LiveData<TrackDataEntity> = _trackingData

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
            if (courier.equals("sicepat", false) ||
                courier.equals("pos", false) ||
                courier.equals("ninja", false)) {
                val result: DataResult<RajaOngkirResponse<TrackResult>> =
                    remoteRepository.retrieveRajaOngkir(awb, courier)
                when (result) {
                    is DataResult.Success -> {
                        val data = result.data?.rajaongkir?.result
                        val value = TrackDataEntity(
                            Summary(
                                data?.summary!!.waybill,
                                data.summary.courierName,
                                data.summary.service,
                                data.summary.status,
                                ""
                            ),
                            Information(
                                data.summary.origin,
                                data.summary.destination,
                                data.summary.shipper,
                                data.summary.receiver

                            ),
                            data.manifestData.map { e -> e.toTracking() }
                        )

                        _trackingData.postValue(value)
                    }
                    is DataResult.Error -> {
                        _onMessageError.postValue(result.errorMessage)
                    }
                    else -> _onMessageError.postValue("Internal Error")
                }
            } else {
                val result: DataResult<BaseResponse<TrackDataEntity>> =
                    remoteRepository.retrieveTrackingNew(awb, courier)
                when (result) {
                    is DataResult.Success -> {
                        _trackingData.value = result.data?.data
                    }
                    is DataResult.Error -> {
                        _onMessageError.postValue(result.errorMessage)
                    }
                    else -> _onMessageError.postValue("Internal Error")
                }
            }

            _isViewLoading.postValue(false)
        }
    }
}