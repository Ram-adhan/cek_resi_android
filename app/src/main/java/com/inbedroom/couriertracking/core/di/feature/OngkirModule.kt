package com.inbedroom.couriertracking.core.di.feature

import com.inbedroom.couriertracking.data.PreferencesManager
import com.inbedroom.couriertracking.data.network.CekOngkirRepository
import com.inbedroom.couriertracking.data.network.CekOngkirRepositoryImpl
import com.inbedroom.couriertracking.data.network.api.OngkirApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class OngkirModule {

    @Provides
    @Singleton
    fun provideOngkirApi(retrofit: Retrofit) = retrofit.create(OngkirApi::class.java)

    @Provides
    @Singleton
    fun provideOngkirRepository(ongkirApi: OngkirApi, preferencesManager: PreferencesManager): CekOngkirRepository =
        CekOngkirRepositoryImpl(ongkirApi, preferencesManager)
}