package com.inbedroom.couriertracking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inbedroom.couriertracking.data.entity.CekOngkirSetupValidation
import com.inbedroom.couriertracking.data.entity.CostRequest
import com.inbedroom.couriertracking.data.entity.OngkirResult
import com.inbedroom.couriertracking.data.network.CekOngkirRepository
import com.inbedroom.couriertracking.data.network.response.DataResult
import kotlinx.coroutines.launch
import javax.inject.Inject

class OngkirViewModel @Inject constructor(
    private val ongkirRepository: CekOngkirRepository
) : ViewModel() {

    companion object {
        const val STATUS_LOADING = 0
        const val STATUS_FINISHED = 1
        const val STATUS_ERROR = 2
    }

    private val _onRequestStatus = MutableLiveData<Int>()
    val onRequestStatus: LiveData<Int> = _onRequestStatus

    private val _ongkirListData = MutableLiveData<List<OngkirResult>>()
    val ongkirListData: LiveData<List<OngkirResult>> = _ongkirListData

    fun checkTariff(
        data: CekOngkirSetupValidation
    ) {
        _onRequestStatus.postValue(STATUS_LOADING)
        val tempData = mutableListOf<OngkirResult>()

        val request = CostRequest(
            data.origin!!.id,
            data.origin!!.type,
            data.destination!!.id,
            data.destination!!.type,
            data.weight,
            data.formattedCourier
        )
        viewModelScope.launch {

            when (val result = ongkirRepository.getTariffList(request)) {
                is DataResult.Success -> {
                    tempData.addAll(result.data!!)
                    if (_onRequestStatus.value != STATUS_FINISHED) {
                        _onRequestStatus.postValue(STATUS_FINISHED)
                    }
                    _ongkirListData.postValue(tempData)
                }
                else -> {
                    if (_onRequestStatus.value != STATUS_ERROR) {
                        _onRequestStatus.postValue(STATUS_ERROR)
                    }
                }

            }
        }
    }
}