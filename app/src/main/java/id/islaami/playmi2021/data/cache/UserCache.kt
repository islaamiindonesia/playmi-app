package id.islaami.playmi2021.data.cache

import com.chibatching.kotpref.KotprefModel
import id.islaami.playmi2021.config.Cache
import id.islaami.playmi2021.data.model.notification.NotificationSetting
import id.islaami.playmi2021.data.model.profile.Profile

class UserCache(
    private val profileCache: Cache<Profile>,
    private val notifSettingCache: Cache<NotificationSetting>
) : KotprefModel() {
    private var token: String by stringPref("")

    /* SETTINGS */
    var hasSeenIntro: Boolean by booleanPref(false)

    var selectedLocale: String by stringPref("id")

    var headerToken: String
        get() = if (token.isNotEmpty()) "Bearer $token" else ""
        set(value) {
            token = value
        }

    var profile: Profile?
        get() = profileCache.load(PROFILE)
        set(value) {
            value?.let { profileCache.save(PROFILE, it) }
        }

    var notificationSetting: NotificationSetting?
        get() = notifSettingCache.load(NOTIF_SETTING)
        set(value) {
            value?.let { notifSettingCache.save(PROFILE, it) }
        }

    companion object {
        private const val PROFILE = "PROFILE"
        private const val NOTIF_SETTING = "NOTIF_SETTING"
    }
}