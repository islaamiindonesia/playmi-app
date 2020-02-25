package com.example.playmi.data.cache

import com.chibatching.kotpref.KotprefModel
import com.example.playmi.config.Cache
import com.example.playmi.data.model.notification.NotificationSetting
import com.example.playmi.data.model.profile.Profile

class UserCache(
    private val profileCache: Cache<Profile>,
    private val notifSettingCache: Cache<NotificationSetting>
) : KotprefModel() {

    var hasSeenIntro: Boolean by booleanPref(false)

    private var token: String by stringPref("")

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