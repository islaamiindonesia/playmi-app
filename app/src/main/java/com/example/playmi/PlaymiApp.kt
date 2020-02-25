package com.example.playmi

import androidx.multidex.MultiDexApplication
import com.chibatching.kotpref.Kotpref
import com.example.playmi.config.CacheLibrary
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PlaymiApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        // initialization of Shared Preferences library
        Kotpref.init(this)

        // initialization of Cache library to allow saving into device
        CacheLibrary.init(this)

        // initialization of Dependency Injection library to allow the use of application context
        startKoin { androidContext(this@PlaymiApp) }
    }
}