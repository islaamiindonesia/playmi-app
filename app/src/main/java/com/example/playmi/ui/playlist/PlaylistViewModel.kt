package com.example.playmi.ui.playlist

import androidx.lifecycle.MutableLiveData
import com.example.playmi.data.model.playlist.Playlist
import com.example.playmi.data.model.video.Video
import com.example.playmi.data.repository.PlaylistRepository
import com.example.playmi.ui.base.BaseViewModel
import com.example.playmi.util.*

class PlaylistViewModel(private val repository: PlaylistRepository) : BaseViewModel() {

    /* PLAYLIST */
    lateinit var createPlaylistResultLd: MutableLiveData<Resource<Any>>
    lateinit var addToPlaylistResultLd: MutableLiveData<Resource<Any>>
    lateinit var removeFromPlaylistResultLd: MutableLiveData<Resource<Any>>
    lateinit var changePlaylistNameResultLd: MutableLiveData<Resource<Any>>
    lateinit var deletePlaylistResultLd: MutableLiveData<Resource<Any>>
    lateinit var getPlaylistResultLd: MutableLiveData<Resource<List<Playlist>>>
    lateinit var getPlaylistVideoResultLd: MutableLiveData<Resource<List<Video>>>

    fun getPlaylists() {
        disposable.add(repository.getAllPlaylist().execute()
            .doOnSubscribe { getPlaylistResultLd.setLoading() }
            .subscribe(
                { result -> getPlaylistResultLd.setSuccess(result) },
                { throwable -> getPlaylistResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun getPlaylistVideo(playlistId: Int) {
        disposable.add(repository.getPlaylistVideo(playlistId).execute()
            .doOnSubscribe { getPlaylistVideoResultLd.setLoading() }
            .subscribe(
                { result -> getPlaylistVideoResultLd.setSuccess(result) },
                { throwable -> getPlaylistVideoResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun createPlaylist(name: String, videoId: Int? = null) {
        disposable.add(repository.createPlaylist(name, videoId).execute()
            .doOnSubscribe { createPlaylistResultLd.setLoading() }
            .subscribe(
                { result -> createPlaylistResultLd.setSuccess(result) },
                { throwable -> createPlaylistResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun changePlaylistName(id: Int, name: String) {
        disposable.add(repository.changePlaylistName(id, name).execute()
            .doOnSubscribe { changePlaylistNameResultLd.setLoading() }
            .subscribe(
                { result -> changePlaylistNameResultLd.setSuccess(result) },
                { throwable -> changePlaylistNameResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun deletePlaylist(id: Int) {
        disposable.add(repository.deletePlaylist(id).execute()
            .doOnSubscribe { deletePlaylistResultLd.setLoading() }
            .subscribe(
                { result -> deletePlaylistResultLd.setSuccess(result) },
                { throwable -> deletePlaylistResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun addToPlaylist(videoId: Int, playlistId: Int) {
        disposable.add(repository.addVideoToPlaylist(videoId, playlistId).execute()
            .doOnSubscribe { addToPlaylistResultLd.setLoading() }
            .subscribe(
                { result -> addToPlaylistResultLd.setSuccess(result) },
                { throwable -> addToPlaylistResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun removeFromPlaylist(videoId: Int, playlistId: Int) {
        disposable.add(repository.removeVideoFromPlaylist(videoId, playlistId).execute()
            .doOnSubscribe { removeFromPlaylistResultLd.setLoading() }
            .subscribe(
                { result -> removeFromPlaylistResultLd.setSuccess(result) },
                { throwable -> removeFromPlaylistResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    /* WATCHLATER */
    lateinit var watchLaterResultLd: MutableLiveData<Resource<Any>>
    lateinit var getLaterVideoResultLd: MutableLiveData<Resource<List<Video>>>
    lateinit var getWatchLaterAmountLd: MutableLiveData<Resource<Int>>
    lateinit var deleteFromLaterResultLd: MutableLiveData<Resource<Any>>

    fun watchLater(videoId: Int) {
        disposable.add(repository.watchLater(videoId).execute()
            .doOnSubscribe { watchLaterResultLd.setLoading() }
            .subscribe(
                { result -> watchLaterResultLd.setSuccess(result) },
                { throwable -> watchLaterResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun getLaterVideos() {
        disposable.add(repository.getAllLater().execute()
            .doOnSubscribe { getLaterVideoResultLd.setLoading() }
            .subscribe(
                { result -> getLaterVideoResultLd.setSuccess(result) },
                { throwable -> getLaterVideoResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun getWatchLaterAmount() {
        disposable.add(repository.getWatchLaterAmount().execute()
            .doOnSubscribe { getWatchLaterAmountLd.setLoading() }
            .subscribe(
                { result -> getWatchLaterAmountLd.setSuccess(result) },
                { throwable -> getWatchLaterAmountLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun deleteFromLater(videoId: Int) {
        disposable.add(repository.deleteFromLater(videoId).execute()
            .doOnSubscribe { deleteFromLaterResultLd.setLoading() }
            .subscribe(
                { result -> deleteFromLaterResultLd.setSuccess(result) },
                { throwable -> deleteFromLaterResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun initPlaylistFragment() {
        changePlaylistNameResultLd = MutableLiveData()
        deletePlaylistResultLd = MutableLiveData()
        getPlaylistResultLd = MutableLiveData()
        getWatchLaterAmountLd = MutableLiveData()

        getWatchLaterAmount()
        getPlaylists()
    }

    fun initPlaylistDetailActivity(playlistId: Int) {
        watchLaterResultLd = MutableLiveData()
        followChannelResultLd = MutableLiveData()
        addToPlaylistResultLd = MutableLiveData()
        removeFromPlaylistResultLd = MutableLiveData()
        getPlaylistVideoResultLd = MutableLiveData()
        getPlaylistResultLd = MutableLiveData()

        getPlaylistVideo(playlistId)
        getPlaylists()
    }

    /* FOLLOW UNFOLLOW */
    lateinit var followChannelResultLd: MutableLiveData<Resource<Any>>

    fun followChannel(id: Int) {
        disposable.add(repository.followChannel(id).execute()
            .doOnSubscribe { followChannelResultLd.setLoading() }
            .subscribe(
                { followChannelResultLd.setSuccess() },
                { throwable -> followChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun unfollowChannel(id: Int) {
        disposable.add(repository.unfollowChannel(id).execute()
            .doOnSubscribe { followChannelResultLd.setLoading() }
            .subscribe(
                { followChannelResultLd.setSuccess() },
                { throwable -> followChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun initWatchLaterActivity() {
        getPlaylistResultLd = MutableLiveData()
        followChannelResultLd = MutableLiveData()
        createPlaylistResultLd = MutableLiveData()
        addToPlaylistResultLd = MutableLiveData()
        getLaterVideoResultLd = MutableLiveData()
        deleteFromLaterResultLd = MutableLiveData()

        getPlaylists()
        getLaterVideos()
    }
}
