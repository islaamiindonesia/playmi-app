package id.islaami.playmi2021.data.repository

import id.islaami.playmi2021.data.api.UserApi
import id.islaami.playmi2021.data.cache.UserCache
import id.islaami.playmi2021.data.model.kotpref.Default
import id.islaami.playmi2021.data.model.profile.LoginBody
import id.islaami.playmi2021.data.model.profile.Profile
import id.islaami.playmi2021.data.model.profile.RegisterBody
import id.islaami.playmi2021.data.model.profile.UpdateProfileBody

class UserRepository(private val userCache: UserCache, private val userApi: UserApi) {
    var hasSeenIntro: Boolean
        get() = userCache.hasSeenIntro
        set(value) {
            userCache.hasSeenIntro = value
        }

    var profile: Profile?
        get() = userCache.profile
        set(value) {
            userCache.profile = value
        }

    var selectedLocale: String
        get() = userCache.selectedLocale
        set(value) {
            userCache.selectedLocale = value
        }

    // Login
    fun login(email: String, fcm: String) =
        userApi.login(LoginBody(email, fcm)).map {
            // save header token and profile data to cache
            userCache.headerToken = it.data?.token.toString()
            userCache.profile = it.data?.user

            it.data
        }

    fun clearCache() {
        userCache.headerToken = ""
        Default.hasLoggedIn = false
    }

    fun isLoggedIn(): Boolean = userCache.headerToken.isNotEmpty()

    fun getProfile() = userApi.getProfile().map {
        userCache.profile = it.data
        it.data
    }

    fun getProfileName() = userApi.getProfile().map {
        // save profile data to cache
        userCache.profile = it.data

        it.data?.fullname
    }

    fun register(
        fullname: String,
        email: String,
        birthdate: String,
        gender: String,
        notifToken: String
    ) = userApi.register(RegisterBody(email, fullname, birthdate, gender, notifToken))
        .map {
            userCache.headerToken = it.data?.token.toString()
            it.data
        }

    fun updateProfile(
        fullname: String,
        email: String,
        birthdate: String,
        gender: String,
    ) = userApi.updateProfile(UpdateProfileBody(email, fullname, birthdate, gender)).map { it.data }

    fun resendEmail(email: String, name: String) = userApi.resendCode(email, name).map { it.data }

    fun verify(email: String) = userApi.verify(email)

    fun notifyOnline() = userApi.notifyOnline()
}