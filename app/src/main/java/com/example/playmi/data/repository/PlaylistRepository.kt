package com.example.playmi.data.repository

import com.example.playmi.data.api.PlaylistApi
import com.example.playmi.data.api.VideoApi

class PlaylistRepository(private val playlistApi: PlaylistApi, private val videoApi: VideoApi) {

    fun createPlaylist(name: String, videoId: Int? = null) =
        playlistApi.createPlaylist(name, videoId)

    fun addVideoToPlaylist(videoId: Int, playlistId: Int) =
        playlistApi.addVideoToPlaylist(videoId, playlistId)

    fun getAllPlaylist() = playlistApi.getAllPlaylist().map { it.data }

    fun getPlaylistVideo(id: Int) = videoApi.getPlaylistVideo(id).map { it.data }

    fun getAllLater() = playlistApi.getWatchLater().map { it.data }

    fun getWatchLaterAmount() = playlistApi.getWatchLater().map { it.data?.size }
}