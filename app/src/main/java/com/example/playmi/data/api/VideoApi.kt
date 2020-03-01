package com.example.playmi.data.api

import com.example.playmi.data.model.ApiResponse
import com.example.playmi.data.model.video.Video
import com.example.playmi.data.model.video.VideoData
import com.example.playmi.util.DEFAULT_LIMIT
import com.example.playmi.util.DEFAULT_VIDEO_FILTER
import io.reactivex.Single
import retrofit2.http.*

interface VideoApi {
    @GET("videos")
    fun getAllVideo(
        @Query("page") page: Int,
        @Query("limit") limit: Int = DEFAULT_LIMIT,
        @Query("filter") filter: String? = DEFAULT_VIDEO_FILTER,
        @Query("category") category: String? = null,
        @Query("query") query: String? = null
    ): Single<ApiResponse<VideoData>>

    @GET("playlists/{id}/videos")
    fun getPlaylistVideo(@Path("id") playlistId: Int): Single<ApiResponse<List<Video>>>

    @GET("videos/follow")
    fun getAllVideoByFollowing(
        @Query("page") page: Int,
        @Query("limit") limit: Int = DEFAULT_LIMIT
    ): Single<ApiResponse<VideoData>>

    @GET("videos/{id}")
    fun getVideo(@Path("id") id: Int): Single<ApiResponse<Video>>

    @POST("videos/{id}/later")
    fun addToLater(@Path("id") videoId: Int): Single<ApiResponse<Any>>

    @DELETE("videos/{id}/later")
    fun deleteFromlater(@Path("id") videoId: Int): Single<ApiResponse<Any>>
}