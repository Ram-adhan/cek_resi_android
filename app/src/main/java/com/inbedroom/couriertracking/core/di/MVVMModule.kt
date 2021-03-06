package com.inbedroom.couriertracking.core.di

import androidx.lifecycle.ViewModelProvider
import com.inbedroom.couriertracking.data.network.TrackingRemoteRepository
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
        historyRepository: HistoryRepository
    ): ViewModelProvider.Factory = ViewModelFactory(trackingRemoteRepository, historyRepository)
}