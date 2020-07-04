package id.islaami.playmi.data.api

import id.islaami.playmi.data.model.ApiMessageResult
import id.islaami.playmi.data.model.ApiResponse
import id.islaami.playmi.data.model.LegalContent
import id.islaami.playmi.data.model.profile.LoginBody
import id.islaami.playmi.data.model.profile.LoginResult
import id.islaami.playmi.data.model.profile.Profile
import id.islaami.playmi.data.model.profile.RegisterBody
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

    @FormUrlEncoded
    @POST("login/google")
    fun loginByGoogle(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("fullname") fullname: String
    ): Single<ApiResponse<LoginResult>>

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

    @GET("tnc")
    fun getTnc(): Single<ApiResponse<LegalContent>>

    /*@FormUrlEncoded
    @PUT("users/profile/update")
    fun updateProfile(
        @Field("fullname") fullname: String,
        @Field("email") email: String,
        @Field("birthdate") birthdate: String,
        @Field("gender") gender: String
    ): Single<ApiMessageResult>

    @FormUrlEncoded
    @PATCH("user/password/update")
    fun updatePassword(@Field("password") password: String): Single<ApiMessageResult>*/
}