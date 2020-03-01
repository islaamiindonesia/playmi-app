package com.example.playmi.data.api

import com.example.playmi.data.model.ApiResponse
import com.example.playmi.data.model.channel.Channel
import com.example.playmi.data.model.channel.FollowStatus
import com.example.playmi.data.model.channel.HideStatus
import com.example.playmi.data.model.video.Video
import io.reactivex.Single
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChannelApi {
    @GET("channels/follow")
    fun getChannelFollow(): Single<ApiResponse<List<Channel>>>

    @GET("channels/hide")
    fun getChannelHidden(): Single<ApiResponse<List<Channel>>>

    @GET("channels/{id}/detail")
    fun getChannelDetail(@Path("id") id: Int): Single<ApiResponse<Channel>>

    @GET("channels/{id}/videos")
    fun getChannelVideos(@Path("id") id: Int): Single<ApiResponse<List<Video>>>

    @GET("channels/{id}/follow/status")
    fun getFollowingStatus(@Path("id") id: Int): Single<ApiResponse<FollowStatus>>

    @GET("channels/{id}/hide/status")
    fun getHideStatus(@Path("id") id: Int): Single<ApiResponse<HideStatus>>

    @POST("channels/{id}/hide")
    fun hideChannel(@Path("id") id: Int): Single<ApiResponse<Any>>

    @DELETE("channels/{id}/show")
    fun showChannel(@Path("id") id: Int): Single<ApiResponse<Any>>

    @POST("channels/{id}/follow")
    fun followChannel(@Path("id") id: Int): Single<ApiResponse<Any>>

    @DELETE("channels/{id}/unfollow")
    fun unfollowChannel(@Path("id") id: Int): Single<ApiResponse<Any>>
}