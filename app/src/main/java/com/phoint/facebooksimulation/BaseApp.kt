package com.phoint.facebooksimulation

import com.phoint.facebooksimulation.di.AppComponent
import com.phoint.facebooksimulation.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

class BaseApp : DaggerApplication() {

    lateinit var instance: BaseApp

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component: AppComponent = DaggerAppComponent.builder().application(this).build()
        component.inject(this)
        return component
    }
}