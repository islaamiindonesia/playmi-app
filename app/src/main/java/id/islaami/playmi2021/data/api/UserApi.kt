package id.islaami.playmi2021.data.api

import id.islaami.playmi2021.data.model.ApiMessageResult
import id.islaami.playmi2021.data.model.ApiResponse
import id.islaami.playmi2021.data.model.profile.LoginBody
import id.islaami.playmi2021.data.model.profile.LoginResult
import id.islaami.playmi2021.data.model.profile.Profile
import id.islaami.playmi2021.data.model.profile.RegisterBody
import io.reactivex.Single
import retrofit2.http.*

interface UserApi {
    @POST("login")
    fun login(@Body loginBody: LoginBody): Single<ApiResponse<LoginResult>>

    @GET("resend")
    fun resendCode(
        @Query("email") email: String,
        @Query("token") token: String
    ): Single<ApiResponse<Any>>

    @POST("user/logout")
    fun logout(): Single<ApiResponse<Any>>

    @FormUrlEncoded
    @PUT("password/forgot")
    fun forgotPassword(
        @Field("email") email: String
    ): Single<ApiMessageResult>

    @FormUrlEncoded
    @PATCH("verify")
    fun verify(
        @Field("email") email: String,
        @Field("verification_number") code: String
    ): Single<ApiResponse<LoginResult>>

    @POST("register")
    fun register(@Body registerBody: RegisterBody): Single<ApiResponse<Profile>>

    @POST("google/register")
    fun registerFromGoogle(@Body registerBody: RegisterBody): Single<ApiResponse<LoginResult>>

    @GET("user/profile")
    fun getProfile(): Single<ApiResponse<Profile>>
}