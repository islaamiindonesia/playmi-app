package id.islaami.playmi.data.repository

import id.islaami.playmi.data.api.UserApi
import id.islaami.playmi.data.cache.UserCache
import id.islaami.playmi.data.model.profile.LoginBody
import id.islaami.playmi.data.model.profile.Profile
import id.islaami.playmi.data.model.profile.RegisterBody

class UserRepository(private val userCache: UserCache, private val userApi: UserApi) {
    var darkMode: Int
        get() = userCache.darkMode
        set(value) {
            userCache.darkMode = value
        }

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
            userCache.headerToken = it.data?.token.toString()
            userCache.profile = it.data?.user
            it.data
        }

    fun clearCache() {
        userCache.headerToken = ""
    }

    fun isLoggedIn(): Boolean = userCache.headerToken.isNotEmpty()

    fun getProfileName() = userApi.getProfile().map {
        userCache.profile = it.data
        it.data?.fullname
    }

    fun register(
        fullname: String,
        email: String,
        birthdate: String,
        gender: String,
        notifToken: String
    ) = userApi.register(RegisterBody(email, fullname, birthdate, gender, notifToken)).map { it }

    fun registerFromGoogle(
        fullname: String,
        email: String,
        birthdate: String,
        gender: String,
        notifToken: String
    ) = userApi.registerFromGoogle(RegisterBody(email, fullname, birthdate, gender, notifToken))
        .map {
            userCache.headerToken = it.data?.token.toString()
            it.data
        }

    fun verify(email: String, code: String) = userApi.verify(email, code).map { it.data }

    fun resendEmail(email: String, token: String) = userApi.resendCode(email, token).map { it.data }
}