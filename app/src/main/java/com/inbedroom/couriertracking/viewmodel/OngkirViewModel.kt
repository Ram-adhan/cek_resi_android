package com.inbedroom.couriertracking.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inbedroom.couriertracking.data.entity.CityEntity
import com.inbedroom.couriertracking.data.entity.OngkirResult
import com.inbedroom.couriertracking.data.network.CekOngkirRepository
import com.inbedroom.couriertracking.data.network.response.DataResult
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class OngkirViewModel @Inject constructor(
    private val ongkirRepository: CekOngkirRepository
) : ViewModel() {

    private val _cityList = MutableLiveData<List<CityEntity>>()
    val cityList: LiveData<List<CityEntity>> = _cityList

    private val _failed = MutableLiveData<String>()
    val failed: LiveData<String> = _failed

    private val _isLoadingData = MutableLiveData<Boolean>()
    val isLoadingData: LiveData<Boolean> = _isLoadingData

    private val _onRequest = MutableLiveData<Boolean>()
    val onRequest: LiveData<Boolean> = _onRequest

    private val _ongkirData = MutableLiveData<List<OngkirResult>>()
    val ongkirData: LiveData<List<OngkirResult>> = _ongkirData

    init {
        _isLoadingData.postValue(true)
        viewModelScope.launch {
            val result = ongkirRepository.getCityList()

            when (result) {
                is DataResult.Success -> {
                    _cityList.postValue(result.data)
                }
                is DataResult.Error -> {
                    _failed.postValue(result.errorMessage)
                }
            }
            _isLoadingData.postValue(false)
        }
    }

    fun getCityList() {
        _isLoadingData.postValue(true)
        viewModelScope.launch {
            val result = ongkirRepository.getCityList(true)

            when (result) {
                is DataResult.Success -> {
                    _cityList.postValue(result.data)
                }
                is DataResult.Error -> {
                    _failed.postValue(result.errorMessage)
                }
            }
            _isLoadingData.postValue(false)
        }
    }

    fun checkTariff(
        originCode: String,
        destinationCode: String,
        weight: Int,
        couriers: List<String>
    ) {
        _onRequest.postValue(true)
        val tempData = mutableListOf<OngkirResult>()
        viewModelScope.launch {
            couriers.forEach {
                val result = ongkirRepository.getTariffList(
                    originCode,
                    destinationCode,
                    weight,
                    it.toLowerCase(Locale.ROOT)
                )

                when (result) {
                    is DataResult.Success -> {
                        tempData.add(result.data!![0])
                        if (_onRequest.value != false){
                            Log.d("viewmodel", "onRequest")
                            _onRequest.postValue(false)
                        }
                        _ongkirData.postValue(tempData)
                    }
                }
            }
        }
    }
}