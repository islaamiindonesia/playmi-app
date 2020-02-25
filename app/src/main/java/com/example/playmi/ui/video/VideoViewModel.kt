package com.example.playmi.ui.video

import androidx.lifecycle.MutableLiveData
import com.example.playmi.data.model.playlist.Playlist
import com.example.playmi.data.model.video.Video
import com.example.playmi.data.repository.ChannelRepository
import com.example.playmi.data.repository.PlaylistRepository
import com.example.playmi.data.repository.VideoRepository
import com.example.playmi.ui.base.BaseViewModel
import com.example.playmi.util.*

class VideoViewModel(
    private val video: VideoRepository,
    private val channel: ChannelRepository,
    private val playlist: PlaylistRepository
) : BaseViewModel() {

    /* WATCH LATER */
    lateinit var watchLaterResultLd: MutableLiveData<Resource<Any>>

    fun watchLater(videoId: Int) {
        disposable.add(video.watchLater(videoId).execute()
            .doOnSubscribe { watchLaterResultLd.setLoading() }
            .subscribe(
                { result -> watchLaterResultLd.setSuccess(result) },
                { throwable -> watchLaterResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    /* PLAYLIST */
    lateinit var getPlaylistsResultLd: MutableLiveData<Resource<List<Playlist>>>
    lateinit var createPlaylistResultLd: MutableLiveData<Resource<Any>>
    lateinit var addToPlaylistResultLd: MutableLiveData<Resource<Any>>

    fun getPlaylists() {
        disposable.add(playlist.getAllPlaylist().execute()
            .doOnSubscribe { getPlaylistsResultLd.setLoading() }
            .subscribe(
                { result -> getPlaylistsResultLd.setSuccess(result) },
                { throwable -> getPlaylistsResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun createPlaylist(name: String, videoId: Int? = null) {
        disposable.add(playlist.createPlaylist(name, videoId).execute()
            .doOnSubscribe { createPlaylistResultLd.setLoading() }
            .subscribe(
                { result -> createPlaylistResultLd.setSuccess(result) },
                { throwable -> createPlaylistResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun addToPlaylist(videoId: Int, playlistId: Int) {
        disposable.add(playlist.addVideoToPlaylist(videoId, playlistId).execute()
            .doOnSubscribe { addToPlaylistResultLd.setLoading() }
            .subscribe(
                { result -> addToPlaylistResultLd.setSuccess(result) },
                { throwable -> addToPlaylistResultLd.setError(throwable.getErrorMessage()) }
            ))
    }


    /* VIDEO */
    lateinit var getVideoResultLd: MutableLiveData<Resource<Video>>

    fun getVideoDetail(id: Int) {
        disposable.add(video.getVideo(id).execute()
            .doOnSubscribe { getVideoResultLd.setLoading() }
            .subscribe(
                { result -> getVideoResultLd.setSuccess(result) },
                { throwable -> getVideoResultLd.setError(throwable.getErrorMessage()) }
            ))
    }


    /* CHANNEL */
    lateinit var getFollowingStatusResultLd: MutableLiveData<Resource<Boolean>>
    lateinit var followChannelResultLd: MutableLiveData<Resource<Any>>

    fun getFollowingStatus(id: Int) {
        disposable.add(channel.getFollowingStatus(id).execute()
            .doOnSubscribe { getFollowingStatusResultLd.setLoading() }
            .subscribe(
                { result -> getFollowingStatusResultLd.setSuccess(result) },
                { throwable -> getFollowingStatusResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun followChannel(id: Int) {
        disposable.add(channel.followChannel(id).execute()
            .doOnSubscribe { followChannelResultLd.setLoading() }
            .subscribe(
                { followChannelResultLd.setSuccess() },
                { throwable -> followChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun unfollowChannel(id: Int) {
        disposable.add(channel.unfollowChannel(id).execute()
            .doOnSubscribe { followChannelResultLd.setLoading() }
            .subscribe(
                { followChannelResultLd.setSuccess() },
                { throwable -> followChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun initVideoDetailActivity(id: Int) {
        watchLaterResultLd = MutableLiveData()
        getFollowingStatusResultLd = MutableLiveData()
        getVideoResultLd = MutableLiveData()
        followChannelResultLd = MutableLiveData()
        createPlaylistResultLd = MutableLiveData()
        addToPlaylistResultLd = MutableLiveData()
        getPlaylistsResultLd = MutableLiveData()

        getPlaylists()
        getVideoDetail(id)
    }
}
