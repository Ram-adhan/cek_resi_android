package com.inbedroom.couriertracking.viewmodel

import android.util.Log
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

    private val _addressList = MutableLiveData<List<AddressItem<AddressEntity>>>()
    val addressList: LiveData<List<AddressItem<AddressEntity>>> = _addressList

    private val _cityList = MutableLiveData<List<AddressEntity>>()
    val cityList: LiveData<List<AddressEntity>> = _cityList

    private val _subdistrictListOrigin = MutableLiveData<List<AddressEntity>>()
    val subDistrictListOrigin: LiveData<List<AddressEntity>> = _subdistrictListOrigin

    private val _subdistrictListDestination = MutableLiveData<List<AddressEntity>>()
    val subDistrictListDestination: LiveData<List<AddressEntity>> = _subdistrictListDestination

    private val _failedLoadData = MutableLiveData<String>()
    val failedLoadData: LiveData<String> = _failedLoadData

    private val tempCityList = mutableListOf<AddressEntity>()
    private val addresses = mutableListOf<AddressItem<Address>>()

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
                    _cityList.postValue(result.data)
                    tempCityList.clear()
                    tempCityList.addAll(result.data!!.asIterable())

                    toAddressCity(result.data)
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
                    if (isOrigin) {
                        _subdistrictListOrigin.postValue(result.data)
                        result.data?.let { toAddressSubOrigin(it) }
                    } else {
                        _subdistrictListDestination.postValue(result.data)
                        result.data?.let { toAddressSubDestination(it) }
                    }
                    _loadingStatus.postValue(FINISHED)
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

        }
    }

    private fun toAddressCity(data: List<AddressEntity>) {
        val newData = data.map { e ->
            val name = if (e.type.equals("kabupaten", true)) "Kab. ${e.name}" else e.name
            val value = Address(
                name, e.addressId, e.type
            )
            AddressItem.City(value)
        }

        val tempAddressList = addresses.dropWhile { e ->
            when (e) {
                is AddressItem.City -> true
                else -> false
            }
        }

        addresses.clear()
        addresses.addAll(tempAddressList)
        addresses
            .addAll(newData)
    }

    private fun toAddressSubOrigin(data: List<AddressEntity>) {
        val newData = data.map { e -> AddressItem.SubOrigin(e.toAddress()) }

        val tempAddressList = addresses.dropWhile { e ->
            when (e) {
                is AddressItem.SubOrigin -> true
                else -> false
            }
        }

        addresses.clear()
        addresses.addAll(tempAddressList)
        addresses
            .addAll(newData)
    }

    private fun toAddressSubDestination(data: List<AddressEntity>) {
        val newData = data.map { e -> AddressItem.SubDestination(e.toAddress()) }

        val tempAddressList = addresses.dropWhile { e ->
            when (e) {
                is AddressItem.SubDestination -> true
                else -> false
            }
        }

        addresses.clear()
        addresses.addAll(tempAddressList)
        addresses
            .addAll(newData)
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