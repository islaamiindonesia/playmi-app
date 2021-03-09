package id.islaami.playmi2021.data.api

import id.islaami.playmi2021.data.model.ApiResponse
import io.reactivex.Single
import retrofit2.http.*

interface HomeApi {
    @PUT("channels/{id}/follow")
    fun followChannel(@Path("id") id: Int): Single<ApiResponse<Any>>

    @PUT("channels/{id}/hide")
    fun hideChannel(@Path("id") id: Int): Single<ApiResponse<Any>>

    @PUT("channels/{id}/unfollow")
    fun unfollowChannel(@Path("id") id: Int): Single<ApiResponse<Any>>

    @FormUrlEncoded
    @PUT("user/watchlater/add")
    fun addToLater(@Field("video_id") videoID: Int): Single<ApiResponse<Any>>
}