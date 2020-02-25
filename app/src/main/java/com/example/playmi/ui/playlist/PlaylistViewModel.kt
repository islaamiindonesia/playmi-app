package com.example.playmi.ui.playlist

import androidx.lifecycle.MutableLiveData
import com.example.playmi.data.model.playlist.Playlist
import com.example.playmi.data.model.video.Video
import com.example.playmi.data.repository.PlaylistRepository
import com.example.playmi.ui.base.BaseViewModel
import com.example.playmi.util.*

class PlaylistViewModel(private val repository: PlaylistRepository) : BaseViewModel() {

    /* PLAYLIST */
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

    /* WATCHLATER */
    lateinit var getLaterVideoResultLd: MutableLiveData<Resource<List<Video>>>
    lateinit var getWatchLaterAmountLd: MutableLiveData<Resource<Int>>

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

    fun initPlaylistFragment() {
        getPlaylistResultLd = MutableLiveData()
        getWatchLaterAmountLd = MutableLiveData()

        getWatchLaterAmount()
        getPlaylists()
    }

    fun initPlaylistDetailActivity(playlistId: Int) {
        getPlaylistVideoResultLd = MutableLiveData()

        getPlaylistVideo(playlistId)
    }

    fun initWatchLaterActivity() {
        getLaterVideoResultLd = MutableLiveData()

        getLaterVideos()
    }
}
