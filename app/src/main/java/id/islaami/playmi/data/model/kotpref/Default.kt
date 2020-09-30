package id.islaami.playmi.data.model.kotpref

import androidx.appcompat.app.AppCompatDelegate
import com.chibatching.kotpref.KotprefModel

object Default : KotprefModel() {
    // default: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    var hasSeenIntro by booleanPref(false)
    var hasLoggedIn by booleanPref(false)
}