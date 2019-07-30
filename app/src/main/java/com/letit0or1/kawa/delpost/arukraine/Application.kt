package com.letit0or1.kawa.delpost.arukraine

import android.app.Application
import com.letit0or1.kawa.delpost.arukraine.di.moduleE
import com.letit0or1.kawa.delpost.arukraine.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Android context
            androidContext(this@Application)
            // modules
            modules(listOf(moduleE, viewModelModule))
        }
    }
}