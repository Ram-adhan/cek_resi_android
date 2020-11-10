package com.inbedroom.couriertracking.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inbedroom.couriertracking.core.extension.connectNetwork
import com.inbedroom.couriertracking.data.PreferencesManager
import com.inbedroom.couriertracking.data.entity.*
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

    companion object{
        const val LOADING = 0
        const val FINISHED = 1
        const val EMPTY = 2
        const val ERROR = 3
    }

    val historiesData: LiveData<List<HistoryEntity>> = historyRepository.getHistories()
    private val _isChanged = MutableLiveData<Boolean>()
    val isChanged: LiveData<Boolean> = _isChanged

    private val _courierList = MutableLiveData<List<Courier>>()
    val courierList: LiveData<List<Courier>> = _courierList

    private val _isLoadingData = MutableLiveData<Boolean>()
    val isLoadingData: LiveData<Boolean> = _isLoadingData

    private val _isLoadingSubdistricts = MutableLiveData<Int>()
    val isLoadingSubDistrict: LiveData<Int> = _isLoadingSubdistricts

    private val _cityList = MutableLiveData<List<AddressEntity>>()
    val cityList: LiveData<List<AddressEntity>> = _cityList

    private val _subdistrictList = MutableLiveData<List<AddressEntity>>()
    val subDistrictList: LiveData<List<AddressEntity>> = _cityList

    private val _addressList = MutableLiveData<List<AddressEntity>>()
    val addressList: LiveData<List<AddressEntity>> = _addressList

    private val _failedLoadData = MutableLiveData<String>()
    val failedLoadData: LiveData<String> = _failedLoadData

    private val _noNetwork = MutableLiveData<Boolean>()
    val noNetwork: LiveData<Boolean> = _noNetwork

    private val tempCityList = mutableListOf<AddressEntity>()
    private val tempSubDistrictList = mutableListOf<AddressEntity>()

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
        viewModelScope.launch {
            val result = ongkirRepository.getCityList(forceUpdate)

            when (result) {
                is DataResult.Success -> {
                    _cityList.postValue(result.data)
                    tempCityList.clear()
                    tempCityList.addAll(result.data!!.asIterable())
                }
                is DataResult.Error -> {
                    _failedLoadData.postValue(result.errorMessage)
                }
            }
            _isLoadingData.postValue(false)
        }
    }

    private fun getSubDistricts(cityId: String){
        viewModelScope.launch {
            val values = tempCityList
            if (values.isNullOrEmpty()){
                Log.d("viewModel", "get subdistrict failed")
                _isLoadingSubdistricts.postValue(ERROR)
            }else{
                Log.d("viewModel", "get subdistrict")
                _isLoadingSubdistricts.postValue(LOADING)
                val result = ongkirRepository.getSubdistrict(cityId)
                when(result){
                    is DataResult.Success -> {
                        _subdistrictList.postValue(result.data)
                        _isLoadingSubdistricts.postValue(FINISHED)
                    }
                    is DataResult.Error -> {
                        if (_isLoadingSubdistricts.value != ERROR) {
                            _isLoadingSubdistricts.postValue(ERROR)
                        }
                    }
                    is DataResult.Empty -> {
                        if (_isLoadingSubdistricts.value != EMPTY) {
                            _isLoadingSubdistricts.postValue(EMPTY)
                        }
                    }
                }
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