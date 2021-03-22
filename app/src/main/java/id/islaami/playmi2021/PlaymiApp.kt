package id.islaami.playmi2021

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import com.chibatching.kotpref.Kotpref
import id.islaami.playmi2021.config.CacheLibrary
import id.islaami.playmi2021.data.model.kotpref.Mode
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/* Initialize libraries and app theme from here */
class PlaymiApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // initialization of Shared Preferences library
        Kotpref.init(this)

        // initialization of Cache library to allow saving into device
        CacheLibrary.init(this)

        setDefaultNightMode(Mode.appMode)

        // initialization of Dependency Injection library to allow the use of application context
        startKoin { androidContext(this@PlaymiApp) }
    }
}