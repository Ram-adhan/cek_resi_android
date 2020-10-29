package com.inbedroom.couriertracking.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inbedroom.couriertracking.data.entity.CityEntity
import com.inbedroom.couriertracking.data.entity.CostRequest
import com.inbedroom.couriertracking.data.network.CekOngkirRepository
import com.inbedroom.couriertracking.data.network.response.BaseResponse
import com.inbedroom.couriertracking.data.network.response.DataResult
import com.inbedroom.couriertracking.data.network.response.RajaOngkirBaseResponse
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

    fun checkTariff(
        originCode: String,
        destinationCode: String,
        weight: Int,
        couriers: List<String>
    ) {
        viewModelScope.launch {
            couriers.forEach {
                val result = ongkirRepository.getTariffList(
                    originCode,
                    destinationCode,
                    weight,
                    it.toLowerCase()
                )

                when (result){
                    is DataResult.Success -> {
                    }
                }
            }
        }
    }
}