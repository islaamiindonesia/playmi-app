package id.islaami.playmi2021.data.api

import id.islaami.playmi2021.data.model.ApiMessageResult
import id.islaami.playmi2021.data.model.ApiResponse
import id.islaami.playmi2021.data.model.profile.*
import io.reactivex.Single
import retrofit2.http.*

interface UserApi {
    @POST("login")
    fun login(@Body loginBody: LoginBody): Single<ApiResponse<LoginResult>>

    @GET("resend")
    fun resendCode(
        @Query("email") email: String,
        @Query("name") name: String
    ): Single<ApiResponse<Any>>

    @GET("https://islaami.id:3000/verifyUser/{email}")
    fun verify(
        @Path("email") email: String
    ): Single<Void>

    @POST("user/logout")
    fun logout(): Single<ApiResponse<Any>>

    @FormUrlEncoded
    @PUT("password/forgot")
    fun forgotPassword(
        @Field("email") email: String
    ): Single<ApiMessageResult>

    @POST("register")
    fun register(@Body registerBody: RegisterBody): Single<ApiResponse<LoginResult>>

    @GET("user/profile")
    fun getProfile(): Single<ApiResponse<Profile>>

    @PUT("user/profile/update")
    fun updateProfile(@Body updateProfileBody: UpdateProfileBody): Single<ApiResponse<Any>>
}