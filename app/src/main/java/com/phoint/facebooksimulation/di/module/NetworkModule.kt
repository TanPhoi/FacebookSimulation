package com.phoint.facebooksimulation.di.module

import com.phoint.facebooksimulation.data.remote.NetworkInterceptor
import com.phoint.facebooksimulation.util.AppConfig
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class NetworkModule {
    companion object {
        private const val BASE_URL_NEW = "https://jsonplaceholder.typicode.com/"
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_NEW)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(NetworkInterceptor())
            .connectTimeout(AppConfig.Constant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(AppConfig.Constant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }
}