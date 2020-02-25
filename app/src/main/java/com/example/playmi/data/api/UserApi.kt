package com.example.playmi.data.api

import com.example.playmi.data.model.ApiMessageResult
import com.example.playmi.data.model.ApiResponse
import com.example.playmi.data.model.profile.Profile
import io.reactivex.Single
import retrofit2.http.*

interface UserApi {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Single<ApiResponse<Profile>>

    @POST("users/logout")
    fun logout(): Single<ApiResponse<Any>>

    @FormUrlEncoded
    @PUT("password/forgot")
    fun forgotPassword(
        @Field("email") email: String
    ): Single<ApiMessageResult>

    @FormUrlEncoded
    @PATCH("verifyuser")
    fun verify(
        @Field("email") email: String,
        @Field("code") code: String
    ): Single<ApiResponse<Any>>

    @FormUrlEncoded
    @PATCH("password/reset")
    fun resetPassword(
        @Field("reset_token") token: String,
        @Field("new_password") password: String
    ): Single<ApiMessageResult>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("fullname") fullname: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("birthdate") birthdate: String,
        @Field("gender") gender: String,
        @Field("notif_token") notifToken: String
    ): Single<ApiResponse<Profile>>

    @FormUrlEncoded
    @GET("user/profile")
    fun getProfile(): Single<ApiResponse<Profile>>

    @FormUrlEncoded
    @PUT("user/profile/update")
    fun updateProfile(
        @Field("fullname") fullname: String,
        @Field("email") email: String,
        @Field("birthdate") birthdate: String,
        @Field("gender") gender: String
    ): Single<ApiMessageResult>

    @FormUrlEncoded
    @PATCH("user/password/update")
    fun updatePassword(@Field("password") password: String): Single<ApiMessageResult>
}