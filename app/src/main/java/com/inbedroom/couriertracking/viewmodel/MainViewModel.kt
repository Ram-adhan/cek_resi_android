package com.inbedroom.couriertracking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inbedroom.couriertracking.data.PreferencesManager
import com.inbedroom.couriertracking.data.entity.Address
import com.inbedroom.couriertracking.data.entity.AddressEntity
import com.inbedroom.couriertracking.data.entity.Courier
import com.inbedroom.couriertracking.data.entity.HistoryEntity
import com.inbedroom.couriertracking.data.network.CekOngkirRepository
import com.inbedroom.couriertracking.data.network.response.DataResult
import com.inbedroom.couriertracking.data.room.HistoryRepository
import com.inbedroom.couriertracking.utils.AddressItem
import kotlinx.coroutines.launch

class MainViewModel(
    private val historyRepository: HistoryRepository,
    private val ongkirRepository: CekOngkirRepository,
    local: PreferencesManager
) : ViewModel() {

    companion object {
        const val LOADING_SUB_ORIGIN = 5
        const val LOADING_SUB_DESTINATION = 6
        const val LOADING_CITY = 0
        const val FINISHED = 10
        const val EMPTY = 11
        const val ERROR = 20
    }


    var historiesData: LiveData<List<HistoryEntity>> = historyRepository.getHistories()

    private val _isChanged = MutableLiveData<Boolean>()
    val isChanged: LiveData<Boolean> = _isChanged

    private val _courierList = MutableLiveData<List<Courier>>()
    val courierList: LiveData<List<Courier>> = _courierList

    private val _loadingStatus = MutableLiveData<Int>()
    val loadingStatus: LiveData<Int> = _loadingStatus

    private val _addressList = MutableLiveData<AddressItem<List<Address>>>()
    val addressList: LiveData<AddressItem<List<Address>>> = _addressList

    private val _cityList = MutableLiveData<List<Address>>()
    val cityList: LiveData<List<Address>> = _cityList

    private val _failedLoadData = MutableLiveData<String>()
    val failedLoadData: LiveData<String> = _failedLoadData

    private val tempCityList = mutableListOf<AddressEntity>()

    init {

        val list = local.readCourierAsset()
        _courierList.postValue(list)

        getCities(false)
    }

    fun getCityList() {
        getCities()
    }

    private fun getCities(forceUpdate: Boolean = true) {
        _loadingStatus.postValue(LOADING_CITY)
        viewModelScope.launch {
            val result = ongkirRepository.getCityList(forceUpdate)

            when (result) {
                is DataResult.Success -> {
                    _cityList.postValue(result.data?.map {
                        val name =
                            if (it.type.equals("kabupaten", true)) "Kab. ${it.name}" else it.name
                        Address(
                            name, it.addressId, "city"
                        )
                    })
                    tempCityList.clear()
                    tempCityList.addAll(
                        result.data!!.asIterable()
                    )
                }
                is DataResult.Error -> {
                    _failedLoadData.postValue(result.errorMessage)
                }
                else -> _failedLoadData.postValue("Unknown Error")
            }
            _loadingStatus.postValue(FINISHED)
        }
    }

    fun getSubDistricts(cityId: String, isOrigin: Boolean) {
        if (isOrigin) {
            _loadingStatus.postValue(LOADING_SUB_ORIGIN)
        } else {
            _loadingStatus.postValue(LOADING_SUB_DESTINATION)
        }

        viewModelScope.launch {
            val result = ongkirRepository.getSubdistrict(cityId)

            when (result) {
                is DataResult.Success -> {
                    val data = result.data?.map { e -> e.toAddress() }
                    if (isOrigin) {
                        toAddressSubOrigin(data)
                    } else {
                        toAddressSubDestination(data)
                    }
                }
                is DataResult.Error -> {
                    if (_loadingStatus.value != ERROR) {
                        _loadingStatus.postValue(ERROR)
                    }
                }
                is DataResult.Empty -> {
                    if (_loadingStatus.value != EMPTY) {
                        _loadingStatus.postValue(EMPTY)
                    }
                }
            }
            _loadingStatus.postValue(FINISHED)
        }
    }

    private fun toAddressSubOrigin(data: List<Address>?) {
        val newData = if (!data.isNullOrEmpty()) {
            AddressItem.SubOrigin(data.toList())
        } else {
            AddressItem.Empty()
        }
        _addressList.postValue(newData)
    }

    private fun toAddressSubDestination(data: List<Address>?) {
        val newData = if (!data.isNullOrEmpty()) {
            AddressItem.SubDestination(data.toList())
        } else {
            AddressItem.Empty()
        }
        _addressList.postValue(newData)
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