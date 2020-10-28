package com.inbedroom.couriertracking.core.di

import com.inbedroom.couriertracking.CourierTrackingApplication
import com.inbedroom.couriertracking.core.di.feature.HistoryModule
import com.inbedroom.couriertracking.core.di.feature.OngkirModule
import com.inbedroom.couriertracking.core.di.feature.TrackingModule
import com.inbedroom.couriertracking.view.CekOngkirActivity
import com.inbedroom.couriertracking.view.MainActivity
import com.inbedroom.couriertracking.view.TrackingDetailActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        MVVMModule::class,
        HistoryModule::class,
        TrackingModule::class,
        OngkirModule::class
    ]
)
interface ApplicationComponent {
    fun inject(application: CourierTrackingApplication)
    fun inject(mainActivity: MainActivity)
    fun inject(trackingDetailActivity: TrackingDetailActivity)
    fun inject(ongkirActivity: CekOngkirActivity)
}