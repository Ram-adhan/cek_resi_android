package com.inbedroom.couriertracking.core.di

import androidx.lifecycle.ViewModelProvider
import com.inbedroom.couriertracking.data.PreferencesManager
import com.inbedroom.couriertracking.data.network.CekOngkirRepository
import com.inbedroom.couriertracking.data.network.TrackingRemoteRepository
import com.inbedroom.couriertracking.data.room.AddressRepository
import com.inbedroom.couriertracking.data.room.HistoryRepository
import com.inbedroom.couriertracking.viewmodel.ViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MVVMModule {

    @Provides
    @Singleton
    fun provideViewModelFactory(
        trackingRemoteRepository: TrackingRemoteRepository,
        historyRepository: HistoryRepository,
        ongkirRepository: CekOngkirRepository,
        preferencesManager: PreferencesManager
    ): ViewModelProvider.Factory = ViewModelFactory(trackingRemoteRepository, historyRepository, ongkirRepository, preferencesManager)
}