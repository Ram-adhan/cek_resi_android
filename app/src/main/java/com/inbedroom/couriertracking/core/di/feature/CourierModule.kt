package com.inbedroom.couriertracking.core.di.feature

import com.inbedroom.couriertracking.data.PreferencesManager
import com.inbedroom.couriertracking.data.network.CourierRepository
import com.inbedroom.couriertracking.data.network.CourierRepositoryImpl
import com.inbedroom.couriertracking.data.network.api.CouriersApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class CourierModule {

    @Provides
    @Singleton
    fun provideCourierApi(retrofit: Retrofit): CouriersApi = retrofit.create(CouriersApi::class.java)

    @Provides
    @Singleton
    fun provideCouriersRepository(couriersApi: CouriersApi, preferencesManager: PreferencesManager): CourierRepository = CourierRepositoryImpl(couriersApi, preferencesManager)

}