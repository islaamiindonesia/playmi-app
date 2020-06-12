package id.islaami.playmi

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import com.chibatching.kotpref.Kotpref
import id.islaami.playmi.config.CacheLibrary
import id.islaami.playmi.data.model.Mode
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PlaymiApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        // initialization of Shared Preferences library
        Kotpref.init(this)

        // initialization of Cache library to allow saving into device
        CacheLibrary.init(this)

        AppCompatDelegate.setDefaultNightMode(Mode.appMode)

        // initialization of Dependency Injection library to allow the use of application context
        startKoin { androidContext(this@PlaymiApp) }
    }
}