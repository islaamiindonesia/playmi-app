package id.islaami.playmi.data.model

import androidx.appcompat.app.AppCompatDelegate
import com.chibatching.kotpref.KotprefModel

object Mode : KotprefModel() {
    // app them follow system preference by default
    var appMode by intPref(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
}