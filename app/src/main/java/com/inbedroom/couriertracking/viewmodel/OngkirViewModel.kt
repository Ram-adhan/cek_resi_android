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
import javax.inject.Inject

class OngkirViewModel @Inject constructor(
    private val ongkirRepository: CekOngkirRepository
) : ViewModel() {
    private val _cityList = MutableLiveData<List<CityEntity>>()
    val cityList: LiveData<List<CityEntity>> = _cityList

    private val _failed = MutableLiveData<Boolean>()
    val failed: LiveData<Boolean> = _failed

    init {
        viewModelScope.launch {
            val result = ongkirRepository.getCityList()

            when (result) {
                is DataResult.Success -> {
                    _cityList.postValue(result.data)
                }
                is DataResult.Error -> {
                    _failed.postValue(true)
                }
            }
        }
    }

    fun checkTariff(
        request: CostRequest,
        originString: String? = null,
        destinationString: String? = null
    ) {
    }
}