package com.inbedroom.couriertracking.core.di

import android.content.Context
import com.inbedroom.couriertracking.BuildConfig
import com.inbedroom.couriertracking.CourierTrackingApplication
import com.inbedroom.couriertracking.data.PreferencesManager
import com.inbedroom.couriertracking.utils.ServiceData
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class ApplicationModule(private val applicationModule: CourierTrackingApplication) {

    @Provides
    @Singleton
    fun provideApplicationContext(): Context = applicationModule

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(context: Context) = PreferencesManager(context)

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ServiceData.BASE_URL)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun createClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(headerInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        this.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    private val headerInterceptor = Interceptor{ chain ->
        val request = chain.request()

        val builder: Request = if (request.header("RajaOngkir") != null){
            request.newBuilder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("key", ServiceData.ONGKIR_API_KEY)
                .build()
        }else{
            request
        }

        return@Interceptor chain.proceed(builder)
    }

}