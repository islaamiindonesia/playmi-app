package id.islaami.playmi.ui.playlist

import androidx.lifecycle.MutableLiveData
import id.islaami.playmi.data.model.playlist.Playlist
import id.islaami.playmi.data.model.video.Video
import id.islaami.playmi.data.repository.PlaylistRepository
import id.islaami.playmi.ui.base.BaseViewModel
import id.islaami.playmi.util.*

class PlaylistViewModel(private val repository: PlaylistRepository) : BaseViewModel() {

    /* PLAYLIST */
    lateinit var createPlaylistResultLd: MutableLiveData<Resource<Playlist>>
    lateinit var addToPlaylistResultLd: MutableLiveData<Resource<Any>>
    lateinit var removeFromPlaylistResultLd: MutableLiveData<Resource<Any>>
    lateinit var changePlaylistNameResultLd: MutableLiveData<Resource<Any>>
    lateinit var deletePlaylistResultLd: MutableLiveData<Resource<Any>>
    lateinit var getPlaylistResultLd: MutableLiveData<Resource<List<Playlist>>>
    lateinit var getPlaylistDetailResultLd: MutableLiveData<Resource<Playlist>>

    fun getPlaylist() {
        disposable.add(repository.getAllPlaylist().execute()
            .doOnSubscribe { getPlaylistResultLd.setLoading() }
            .subscribe(
                { result -> getPlaylistResultLd.setSuccess(result) },
                { throwable -> getPlaylistResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun getPlaylistDetail(playlistId: Int) {
        disposable.add(repository.getPlaylist(playlistId).execute()
            .doOnSubscribe { getPlaylistDetailResultLd.setLoading() }
            .subscribe(
                { result -> getPlaylistDetailResultLd.setSuccess(result) },
                { throwable -> getPlaylistDetailResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun createPlaylist(name: String, videoId: Int? = null) {
        disposable.add(repository.create(name, videoId).execute()
            .doOnSubscribe { createPlaylistResultLd.setLoading() }
            .subscribe(
                { result -> createPlaylistResultLd.setSuccess(result) },
                { throwable -> createPlaylistResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun changePlaylistName(id: Int, name: String) {
        disposable.add(repository.changeName(id, name).execute()
            .doOnSubscribe { changePlaylistNameResultLd.setLoading() }
            .subscribe(
                { result ->
                    if (result.message == ERROR_DATA_NOT_FOUND) changePlaylistNameResultLd.setError(
                        ERROR_DATA_NOT_FOUND
                    )
                    else changePlaylistNameResultLd.setSuccess(result.data)
                },
                { throwable -> changePlaylistNameResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun deletePlaylist(id: Int) {
        disposable.add(repository.delete(id).execute()
            .doOnSubscribe { deletePlaylistResultLd.setLoading() }
            .subscribe(
                { result -> deletePlaylistResultLd.setSuccess(result) },
                { throwable -> deletePlaylistResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun addToPlaylist(videoId: Int, playlistId: Int) {
        disposable.add(repository.addVideo(videoId, playlistId).execute()
            .doOnSubscribe { addToPlaylistResultLd.setLoading() }
            .subscribe(
                { result -> addToPlaylistResultLd.setSuccess(result) },
                { throwable -> addToPlaylistResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun removeFromPlaylist(videoId: Int, playlistId: Int) {
        disposable.add(repository.removeVideo(videoId, playlistId).execute()
            .doOnSubscribe { removeFromPlaylistResultLd.setLoading() }
            .subscribe(
                { result -> removeFromPlaylistResultLd.setSuccess(result) },
                { throwable -> removeFromPlaylistResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    /* WATCHLATER */
    lateinit var watchLaterResultLd: MutableLiveData<Resource<Any>>
    lateinit var watchLaterVideoResultLd: MutableLiveData<Resource<List<Video>>>
    lateinit var getWatchLaterAmountLd: MutableLiveData<Resource<Int>>
    lateinit var deleteFromLaterResultLd: MutableLiveData<Resource<Any>>

    fun addWatchLater(videoId: Int) {
        disposable.add(repository.addLater(videoId).execute()
            .doOnSubscribe { watchLaterResultLd.setLoading() }
            .subscribe(
                { result -> watchLaterResultLd.setSuccess(result) },
                { throwable -> watchLaterResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun getWatchLater() {
        disposable.add(repository.getWatchLater().execute()
            .doOnSubscribe { watchLaterVideoResultLd.setLoading() }
            .subscribe(
                { result -> watchLaterVideoResultLd.setSuccess(result) },
                { throwable -> watchLaterVideoResultLd.setError(throwable.getErrorMessage()) }
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
        disposable.add(repository.removeLater(videoId).execute()
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
        getPlaylist()
    }

    fun initPlaylistDetailActivity(playlistId: Int) {
        createPlaylistResultLd = MutableLiveData()
        changePlaylistNameResultLd = MutableLiveData()
        deletePlaylistResultLd = MutableLiveData()
        watchLaterResultLd = MutableLiveData()
        followChannelResultLd = MutableLiveData()
        addToPlaylistResultLd = MutableLiveData()
        removeFromPlaylistResultLd = MutableLiveData()
        getPlaylistDetailResultLd = MutableLiveData()
        getPlaylistResultLd = MutableLiveData()

        getPlaylistDetail(playlistId)
        getPlaylist()
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
        watchLaterVideoResultLd = MutableLiveData()
        deleteFromLaterResultLd = MutableLiveData()

        getPlaylist()
        getWatchLater()
    }
}
