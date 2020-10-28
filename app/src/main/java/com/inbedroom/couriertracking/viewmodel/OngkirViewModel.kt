package com.inbedroom.couriertracking.viewmodel

import androidx.lifecycle.ViewModel
import com.inbedroom.couriertracking.data.network.CekOngkirRepository
import javax.inject.Inject

class OngkirViewModel @Inject constructor(
    private val ongkirRepository: CekOngkirRepository
): ViewModel() {
}