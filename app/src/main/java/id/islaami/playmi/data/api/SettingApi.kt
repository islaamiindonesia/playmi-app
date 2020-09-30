package id.islaami.playmi.data.api

import id.islaami.playmi.data.model.ApiResponse
import id.islaami.playmi.data.model.setting.LegalityContent
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface SettingApi {
    @FormUrlEncoded
    @POST("reports/add")
    fun addReport(
        @Field("description") description: String,
        @Field("image_url") imageUrl: String
    ): Single<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("recommendations/add")
    fun addRecommendation(
        @Field("channel_name") channelName: String,
        @Field("channel_url") channelUrl: String
    ): Single<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("insights/add")
    fun addInsight(
        @Field("detail") detail: String
    ): Single<ApiResponse<Any>>

    @GET("about")
    fun aboutApp(): Single<ApiResponse<LegalityContent>>

    @GET("cooperation")
    fun cooperation(): Single<ApiResponse<LegalityContent>>

    @GET("tnc")
    fun userTNC(): Single<ApiResponse<LegalityContent>>

    @GET("privacy")
    fun privacyPolicy(): Single<ApiResponse<LegalityContent>>
}