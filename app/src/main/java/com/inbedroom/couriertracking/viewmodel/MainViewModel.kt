package com.inbedroom.couriertracking.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inbedroom.couriertracking.core.extension.connectNetwork
import com.inbedroom.couriertracking.data.PreferencesManager
import com.inbedroom.couriertracking.data.entity.AddressEntity
import com.inbedroom.couriertracking.data.entity.CityEntity
import com.inbedroom.couriertracking.data.entity.Courier
import com.inbedroom.couriertracking.data.entity.HistoryEntity
import com.inbedroom.couriertracking.data.network.CekOngkirRepository
import com.inbedroom.couriertracking.data.network.response.DataResult
import com.inbedroom.couriertracking.data.room.AddressRepository
import com.inbedroom.couriertracking.data.room.HistoryRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val ongkirRepository: CekOngkirRepository,
    local: PreferencesManager
) : ViewModel() {

    val historiesData: LiveData<List<HistoryEntity>> = historyRepository.getHistories()
    private val _isChanged = MutableLiveData<Boolean>()
    val isChanged: LiveData<Boolean> = _isChanged

    private val _courierList = MutableLiveData<List<Courier>>()
    val courierList: LiveData<List<Courier>> = _courierList

    private val _isLoadingData = MutableLiveData<Boolean>()
    val isLoadingData: LiveData<Boolean> = _isLoadingData

    private val _isLoadingSubdistricts = MutableLiveData<Boolean>()
    val isLoadingSubDistrict: LiveData<Boolean> = _isLoadingSubdistricts

    private val _cityList = MutableLiveData<List<CityEntity>>()
    val cityList: LiveData<List<CityEntity>> = _cityList

    private val _failedLoadData = MutableLiveData<String>()
    val failedLoadData: LiveData<String> = _failedLoadData

    private val _noNetwork = MutableLiveData<Boolean>()
    val noNetwork: LiveData<Boolean> = _noNetwork

    init {

        val list = local.readCourierAsset()
        _courierList.postValue(list)

        getCities(false)
    }

    fun getCityList(context: Context) {
        if (context.connectNetwork()) {
            _isLoadingData.postValue(true)
            _noNetwork.postValue(false)
            getCities()
        } else {
            _noNetwork.postValue(true)
        }
    }

    private fun getCities(forceUpdate: Boolean = true) {
        val tempCityList = mutableListOf<CityEntity>()

        viewModelScope.launch {
            val result = ongkirRepository.getCityList(forceUpdate)

            when (result) {
                is DataResult.Success -> {
                    _cityList.postValue(result.data)
                    tempCityList.addAll(result.data!!.asIterable())
                }
                is DataResult.Error -> {
                    _failedLoadData.postValue(result.errorMessage)
                }
            }
            _isLoadingData.postValue(false)

            if (!tempCityList.isNullOrEmpty()) {
                _isLoadingSubdistricts.postValue(true)

                tempCityList.forEach { cityEntity ->

                    val subDistrictResult = ongkirRepository.getSubdistrict(cityEntity.cityId)

                }
            } else {
                _failedLoadData.postValue("Failed load sub-districts")
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