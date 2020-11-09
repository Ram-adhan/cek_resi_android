package com.inbedroom.couriertracking.core.di.feature

import com.inbedroom.couriertracking.data.PreferencesManager
import com.inbedroom.couriertracking.data.room.AddressDao
import com.inbedroom.couriertracking.data.room.AddressRepository
import com.inbedroom.couriertracking.data.room.AddressRepositoryImpl
import com.inbedroom.couriertracking.data.room.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AddressModule {

    @Provides
    @Singleton
    fun provideAddressDao(db: AppDatabase) = db.addressDao()

    @Provides
    fun provideAddressRepository(dao: AddressDao, prefManager: PreferencesManager): AddressRepository = AddressRepositoryImpl(dao, prefManager)

}