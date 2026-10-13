package com.example.movilsdd

import android.app.Application
import com.example.movilsdd.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MovilSddApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MovilSddApp)
            modules(appModules)
        }
    }
}
