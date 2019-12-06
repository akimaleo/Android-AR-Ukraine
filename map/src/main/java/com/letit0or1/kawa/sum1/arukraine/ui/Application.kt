package com.letit0or1.kawa.sum1.arukraine.ui

import android.app.Application
import com.letit0or1.kawa.sum1.arukraine.di.dataModule
import com.letit0or1.kawa.sum1.arukraine.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Android context
            androidContext(this@Application)
            // modules
            modules(listOf(dataModule, viewModelModule))
        }
    }
}