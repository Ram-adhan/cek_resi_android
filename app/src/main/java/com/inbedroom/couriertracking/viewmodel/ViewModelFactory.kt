package com.inbedroom.couriertracking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inbedroom.couriertracking.data.PreferencesManager
import com.inbedroom.couriertracking.data.network.CekOngkirRepository
import com.inbedroom.couriertracking.data.network.TrackingRemoteRepository
import com.inbedroom.couriertracking.data.room.HistoryRepository
import java.lang.IllegalArgumentException
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class ViewModelFactory @Inject constructor(
    private val remoteRepository: TrackingRemoteRepository,
    private val historyRepository: HistoryRepository,
    private val ongkirRepository: CekOngkirRepository,
    private val preferencesManager: PreferencesManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TrackingViewModel::class.java) -> TrackingViewModel(
                remoteRepository, historyRepository
            ) as T

            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(
                historyRepository, ongkirRepository, preferencesManager
            ) as T

            modelClass.isAssignableFrom(OngkirViewModel::class.java) -> OngkirViewModel(
                ongkirRepository
            ) as T

            else -> throw IllegalArgumentException("Unknown ViewModel Class ${modelClass::class.java.simpleName}")
        }
    }


}