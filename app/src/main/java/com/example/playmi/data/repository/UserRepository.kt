package com.example.playmi.data.repository

import android.util.Log
import com.example.playmi.data.api.UserApi
import com.example.playmi.data.cache.UserCache
import com.example.playmi.data.model.ApiMessageResult
import com.example.playmi.data.model.ApiResponse
import com.example.playmi.data.model.profile.Profile
import io.reactivex.Single

class UserRepository(private val userCache: UserCache, private val userApi: UserApi) {

    var hasSeenIntro: Boolean
        get() = userCache.hasSeenIntro
        set(value) {
            userCache.hasSeenIntro = value
        }

    fun clearCache() {
        userCache.clear()
    }

    fun isLoggedIn(): Boolean = userCache.headerToken.isNotEmpty()

    fun getProfile(): Single<ApiResponse<Profile>> =
        userApi.getProfile().map {
            userCache.profile = it.data
            it
        }

    fun login(email: String, password: String) =
        userApi.login(email, password)
            .map {
                userCache.headerToken = it.data?.authToken.toString()
                it.data
            }

    fun forgotPassword(email: String): Single<ApiMessageResult> =
        userApi.forgotPassword(email).map { it }

    fun resetPassword(token: String, password: String): Single<ApiMessageResult> =
        userApi.resetPassword(token, password).map { it }

    fun register(
        fullname: String,
        email: String,
        password: String,
        birthdate: String,
        gender: String,
        notifToken: String
    ) =
        userApi.register(fullname, email, password, birthdate, gender, notifToken).map { it }

    fun updateProfile(
        fullname: String,
        email: String,
        birthdate: String,
        gender: String
    ): Single<ApiMessageResult> =
        userApi.updateProfile(fullname, email, birthdate, gender).map { it }

    fun verify(email: String, code: String) = userApi.verify(email, code)

    fun logout() = userApi.logout().map { it.data }
}