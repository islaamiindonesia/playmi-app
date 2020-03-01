package com.example.playmi.data.repository

import com.example.playmi.data.api.ChannelApi
import com.example.playmi.data.api.PlaylistApi
import com.example.playmi.data.api.VideoApi

class PlaylistRepository(
    private val playlist: PlaylistApi,
    private val video: VideoApi,
    private val channel: ChannelApi
) {
    fun watchLater(videoId: Int) = video.addToLater(videoId)

    fun createPlaylist(name: String, videoId: Int? = null) =
        playlist.createPlaylist(name, videoId)

    fun addVideoToPlaylist(videoId: Int, playlistId: Int) =
        playlist.addVideoToPlaylist(videoId, playlistId)

    fun removeVideoFromPlaylist(videoId: Int, playlistId: Int) =
        playlist.removeVideoFromPlaylist(videoId, playlistId)

    fun changePlaylistName(playlistId: Int, playlistName: String) =
        playlist.changePlaylistName(playlistId, playlistName)

    fun deletePlaylist(playlistId: Int) = playlist.deletePlaylist(playlistId)

    fun getAllPlaylist() = playlist.getAllPlaylist().map { it.data }

    fun getPlaylistVideo(id: Int) = video.getPlaylistVideo(id).map { it.data }

    fun getAllLater() = playlist.getWatchLater().map { it.data }

    fun getWatchLaterAmount() = playlist.getWatchLater().map { it.data?.size }

    fun deleteFromLater(videoId: Int) = video.deleteFromlater(videoId)

    fun followChannel(id: Int) = channel.followChannel(id)

    fun unfollowChannel(id: Int) = channel.unfollowChannel(id)
}