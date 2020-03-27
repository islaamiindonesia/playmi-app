package id.islaami.playmi.data.repository

import id.islaami.playmi.data.api.ChannelApi
import id.islaami.playmi.data.api.PlaylistApi
import id.islaami.playmi.data.api.VideoApi

class PlaylistRepository(
    private val playlist: PlaylistApi,
    private val video: VideoApi,
    private val channel: ChannelApi
) {
    /* WATCHLATER */
    fun getWatchLater() = playlist.getWatchLater().map { it.data }

    fun getWatchLaterAmount() = playlist.getWatchLater().map { it.data?.size }

    fun addLater(videoId: Int) = video.addWatchLater(videoId)

    fun removeLater(videoId: Int) = video.deleteFromlater(videoId)

    /* PLAYLIST */
    fun create(name: String, videoId: Int? = null) = playlist.create(name, videoId).map { it.data }

    fun addVideo(videoId: Int, id: Int) =
        playlist.addVideo(id = id, videoId = videoId)

    fun removeVideo(videoId: Int, id: Int) =
        playlist.removeVideo(id = id, videoId = videoId)

    fun changeName(id: Int, playlistName: String) =
        playlist.changeName(id, playlistName)

    fun delete(id: Int) = playlist.delete(id)

    fun getAllPlaylist() = playlist.getAllPlaylist().map { it.data }

    fun getPlaylist(id: Int) = playlist.getPlaylist(id).map { it.data }

    fun followChannel(id: Int) = channel.followChannel(id)

    fun unfollowChannel(id: Int) = channel.unfollowChannel(id)
}