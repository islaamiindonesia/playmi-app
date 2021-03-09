package id.islaami.playmi2021.data.model.kotpref

import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import com.chibatching.kotpref.KotprefModel

object Mode : KotprefModel() {
    // default: AppCompatDelegate.MODE_NIGHT_NO
    var appMode: Int by intPref(default = MODE_NIGHT_NO)
}