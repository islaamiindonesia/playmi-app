package id.islaami.playmi2021.data.api

import id.islaami.playmi2021.data.model.ApiResponse
import id.islaami.playmi2021.data.model.playlist.AddVideoBody
import id.islaami.playmi2021.data.model.playlist.Playlist
import id.islaami.playmi2021.data.model.video.Video
import io.reactivex.Single
import retrofit2.http.*

interface PlaylistApi {
    @FormUrlEncoded
    @POST("user/playlists/add")
    fun create(
        @Query("video") videoId: Int? = null,
        @Field("name") name: String
    ): Single<ApiResponse<Any>>

    @FormUrlEncoded
    @PUT("user/playlists/{id}/addvideo")
    fun addVideo(
        @Path("id") id: Int,
        @Field("video_id") videoId: Int
    ): Single<ApiResponse<Any>>

    @PUT("user/playlists/addvideo")
    fun addVideo(@Body addVideoBody: AddVideoBody): Single<ApiResponse<Any>>

    @FormUrlEncoded
    @PUT("user/playlists/{id}/removevideo")
    fun removeVideo(
        @Path("id") id: Int,
        @Field("video_id") videoId: Int
    ): Single<ApiResponse<Any>>

    @FormUrlEncoded
    @PATCH("user/playlists/{id}")
    fun changeName(
        @Path("id") id: Int,
        @Field("name") playlistName: String
    ): Single<ApiResponse<Any>>

    @DELETE("user/playlists/{id}")
    fun delete(@Path("id") playlistId: Int): Single<ApiResponse<Any>>

    @GET("user/playlists")
    fun getAllPlaylist(): Single<ApiResponse<List<Playlist>>>

    @GET("user/playlists/{id}")
    fun getPlaylist(
        @Path("id") playlistId: Int,
        @Query("query") query: String? = ""
    ): Single<ApiResponse<Playlist>>

    @GET("user/watchlater")
    fun getWatchLater(): Single<ApiResponse<List<Video>>>
}