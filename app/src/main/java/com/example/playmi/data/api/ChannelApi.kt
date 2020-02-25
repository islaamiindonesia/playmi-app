package com.example.playmi.data.api

import com.example.playmi.data.model.ApiResponse
import com.example.playmi.data.model.channel.Channel
import com.example.playmi.data.model.channel.FollowStatus
import com.example.playmi.data.model.channel.HideStatus
import com.example.playmi.data.model.video.Video
import com.example.playmi.data.model.video.VideoData
import com.example.playmi.util.DEFAULT_LIMIT
import com.example.playmi.util.DEFAULT_VIDEO_FILTER
import io.reactivex.Single
import retrofit2.http.*

interface ChannelApi {
    @GET("channels")
    fun getAllChannel(
        @Query("page") page: Int,
        @Query("limit") limit: Int = DEFAULT_LIMIT,
        @Query("filter") filter: String? = DEFAULT_VIDEO_FILTER,
        @Query("query") query: String? = null
    ): Single<ApiResponse<VideoData>>

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

    @POST("channels/{id}/follow")
    fun followChannel(@Path("id") id: Int): Single<ApiResponse<Any>>

    @DELETE("channels/{id}/unfollow")
    fun unfollowChannel(@Path("id") id: Int): Single<ApiResponse<Any>>
}