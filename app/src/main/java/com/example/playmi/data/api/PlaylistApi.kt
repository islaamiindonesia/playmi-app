package com.example.playmi.data.api

import com.example.playmi.data.model.ApiResponse
import com.example.playmi.data.model.playlist.Playlist
import com.example.playmi.data.model.video.Video
import io.reactivex.Single
import retrofit2.http.*

interface PlaylistApi {
    @FormUrlEncoded
    @POST("playlists/create")
    fun createPlaylist(
        @Field("name") name: String,
        @Field("video_id") videoId: Int? = null
    ): Single<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("videos/{id}/add")
    fun addVideoToPlaylist(
        @Path("id") videoId: Int,
        @Field("playlist_id") playlistId: Int
    ): Single<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("videos/{id}/remove")
    fun removeVideoFromPlaylist(
        @Path("id") videoId: Int,
        @Field("playlist_id") playlistId: Int
    ): Single<ApiResponse<Any>>

    @FormUrlEncoded
    @PATCH("playlists/{id}")
    fun changePlaylistName(
        @Path("id") playlistId: Int,
        @Field("name") playlistName: String
    ): Single<ApiResponse<Any>>

    @DELETE("playlists/{id}")
    fun deletePlaylist(@Path("id") playlistId: Int): Single<ApiResponse<Any>>

    @GET("playlists")
    fun getAllPlaylist(): Single<ApiResponse<List<Playlist>>>

    @GET("playlists/later")
    fun getWatchLater(): Single<ApiResponse<List<Video>>>
}