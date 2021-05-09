package com.inbedroom.couriertracking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inbedroom.couriertracking.data.PreferencesManager
import com.inbedroom.couriertracking.data.entity.Courier
import com.inbedroom.couriertracking.data.entity.HistoryEntity
import com.inbedroom.couriertracking.data.entity.SimpleLocation
import com.inbedroom.couriertracking.data.network.CekOngkirRepository
import com.inbedroom.couriertracking.data.network.CourierRepository
import com.inbedroom.couriertracking.data.network.response.DataResult
import com.inbedroom.couriertracking.data.room.HistoryRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val historyRepository: HistoryRepository,
    private val ongkirRepository: CekOngkirRepository,
    private val courierRepository: CourierRepository,
    local: PreferencesManager
) : ViewModel() {

    companion object {
        const val LOADING_CITY_ORIGIN_START = 0
        const val LOADING_CITY_DESTINATION_START = 1
        const val LOADING_CITY_ORIGIN_FINISHED = 10
        const val LOADING_CITY_DESTINATION_FINISHED = 11
        const val EMPTY = 110
        const val ERROR = 200
    }


    var historiesData: LiveData<List<HistoryEntity>> = historyRepository.getHistories()

    private val _isChanged = MutableLiveData<Boolean>()
    val isChanged: LiveData<Boolean> = _isChanged

    private val _courierList = MutableLiveData<List<Courier>>()
    val courierList: LiveData<List<Courier>> = _courierList

    private val _loadingStatus = MutableLiveData<Int>()
    val loadingStatus: LiveData<Int> = _loadingStatus

    private val _failedLoadData = MutableLiveData<String>()
    val failedLoadData: LiveData<String> = _failedLoadData

    private val _updateCouriers = MutableLiveData<Int>()
    val updateCouriers: LiveData<Int> = _updateCouriers

    private val _locationOriginData = MutableLiveData<List<SimpleLocation>>()
    val locationOriginData: LiveData<List<SimpleLocation>> = _locationOriginData

    private val _locationDestinationData = MutableLiveData<List<SimpleLocation>>()
    val locationDestinationData: LiveData<List<SimpleLocation>> = _locationDestinationData

    init {

        val list = local.readCourierAsset()
        _courierList.postValue(list)

        _updateCouriers.postValue(1)

        getCouriers()
    }

    fun getCouriers(){
        viewModelScope.launch {

            when (val couriers = courierRepository.getCouriers()) {
                is DataResult.Success -> {
                    _courierList.postValue(couriers.data!!)
                    _updateCouriers.postValue(0)
                }
                else -> _updateCouriers.postValue(0)
            }
        }
    }

    fun getLocations(param: String, isOrigin: Boolean) {

        if (isOrigin){
            _loadingStatus.postValue(LOADING_CITY_ORIGIN_START)
        }else{
            _loadingStatus.postValue(LOADING_CITY_DESTINATION_START)
        }

        viewModelScope.launch {

            when (val result = ongkirRepository.getLocationList(param)) {
                is DataResult.Success -> {
                    if (isOrigin)
                        _locationOriginData.postValue(result.data!!)
                    else
                        _locationDestinationData.postValue(result.data!!)
                }

                is DataResult.Empty -> {
                    _loadingStatus.postValue(EMPTY)
                    if (isOrigin)
                        _locationOriginData.postValue(listOf())
                    else
                        _locationDestinationData.postValue(listOf())
                }

                else -> {
                    if (isOrigin)
                        _locationOriginData.postValue(listOf())
                    else
                        _locationDestinationData.postValue(listOf())
                }
            }

            if (isOrigin){
                _loadingStatus.postValue(LOADING_CITY_ORIGIN_FINISHED)
            }else{
                _loadingStatus.postValue(LOADING_CITY_DESTINATION_FINISHED)
            }
        }
    }


    fun deleteHistory(awb: String) {
        viewModelScope.launch {
            historyRepository.deleteHistory(awb)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.deleteAll()
        }
    }

    fun editHistoryTitle(awb: String, title: String?) {
        if (title.isNullOrEmpty()) {
            _isChanged.postValue(false)
        } else {
            viewModelScope.launch {
                historyRepository.changeTitle(awb, title)
                _isChanged.postValue(true)
            }
        }
    }
}