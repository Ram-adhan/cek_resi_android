package com.inbedroom.couriertracking.core.di.feature

import android.content.Context
import com.inbedroom.couriertracking.data.room.HistoryDao
import com.inbedroom.couriertracking.data.room.AppDatabase
import com.inbedroom.couriertracking.data.room.HistoryRepository
import com.inbedroom.couriertracking.data.room.HistoryRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class HistoryModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context) = AppDatabase.invoke(context)

    @Singleton
    @Provides
    fun provideDao(db: AppDatabase) = db.historyDao()

    @Provides
    fun provideHistoryRepository(dao: HistoryDao): HistoryRepository = HistoryRepositoryImpl(dao)

}