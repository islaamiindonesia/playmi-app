package com.example.playmi.data.api

import com.example.playmi.data.model.ApiResponse
import com.example.playmi.data.model.channel.FollowStatus
import io.reactivex.Single
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HomeApi {
    @GET("channels/{id}/follow/status")
    fun getFollowingStatus(@Path("id") id: Int): Single<ApiResponse<FollowStatus>>

    @POST("channels/{id}/follow")
    fun followChannel(@Path("id") id: Int): Single<ApiResponse<Any>>

    @POST("channels/{id}/hide")
    fun hideChannel(@Path("id") id: Int): Single<ApiResponse<Any>>

    @DELETE("channels/{id}/unfollow")
    fun unfollowChannel(@Path("id") id: Int): Single<ApiResponse<Any>>

    @POST("videos/{id}/later")
    fun addToLater(@Path("id") videoId: Int): Single<ApiResponse<Any>>
}