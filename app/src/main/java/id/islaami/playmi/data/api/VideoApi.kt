package id.islaami.playmi.data.api

import id.islaami.playmi.data.model.ApiResponse
import id.islaami.playmi.data.model.video.Video
import io.reactivex.Single
import retrofit2.http.*

interface VideoApi {
    @GET("videos")
    fun getAllVideo(
        @Query("page") page: Int,
        @Query("query") query: String? = null
    ): Single<ApiResponse<List<Video>>>

    @GET("channels/{id}/videos")
    fun getAllVideoByChannel(
        @Path("id") id: Int,
        @Query("page") page: Int
    ): Single<ApiResponse<List<Video>>>

    @GET("categories/{id}/videos")
    fun getAllVideoByCategory(
        @Path("id") id: Int,
        @Query("page") page: Int
    ): Single<ApiResponse<List<Video>>>

    @GET("videos/following")
    fun getAllVideoByFollowing(@Query("page") page: Int): Single<ApiResponse<List<Video>>>

    @GET("videos/{id}")
    fun getVideo(@Path("id") id: Int): Single<ApiResponse<Video>>

    @FormUrlEncoded
    @PUT("user/watchlater/add")
    fun addWatchLater(@Field("video_id") videoId: Int): Single<ApiResponse<Any>>

    @FormUrlEncoded
    @PUT("user/watchlater/remove")
    fun deleteFromlater(@Field("video_id") videoId: Int): Single<ApiResponse<Any>>
}