package com.inbedroom.couriertracking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inbedroom.couriertracking.data.entity.OngkirResult
import com.inbedroom.couriertracking.data.network.CekOngkirRepository
import com.inbedroom.couriertracking.data.network.response.DataResult
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class OngkirViewModel @Inject constructor(
    private val ongkirRepository: CekOngkirRepository
) : ViewModel() {

    private val _onRequest = MutableLiveData<Boolean>()
    val onRequest: LiveData<Boolean> = _onRequest

    private val _ongkirListData = MutableLiveData<List<OngkirResult>>()
    val ongkirListData: LiveData<List<OngkirResult>> = _ongkirListData

    private val _ongkirData = MutableLiveData<OngkirResult>()
    val ongkirData: LiveData<OngkirResult> = _ongkirData

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
                            _onRequest.postValue(false)
                        }
                        _ongkirListData.postValue(tempData)
                        _ongkirData.postValue(result.data[0])
                    }
                }
            }
        }
    }
}