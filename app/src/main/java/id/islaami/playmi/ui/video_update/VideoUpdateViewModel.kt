package id.islaami.playmi.ui.video_update

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import id.islaami.playmi.data.model.video.Video
import id.islaami.playmi.data.repository.VideoRepository
import id.islaami.playmi.ui.base.BaseViewModel
import id.islaami.playmi.ui.datafactory.VideoUpdateDataFactory
import id.islaami.playmi.util.*

class VideoUpdateViewModel(
    private val repository: VideoRepository
) : BaseViewModel() {
    lateinit var networkStatusLd: LiveData<Resource<Unit>>

    /* VIDEO */
    lateinit var videoUpdatePagedListResultLd: LiveData<PagedList<Video>>
    lateinit var videoUpdateDataFactory: VideoUpdateDataFactory

    fun getAllVideo() {
        videoUpdateDataFactory = VideoUpdateDataFactory(disposable, repository)

        networkStatusLd = videoUpdateDataFactory.getNetworkStatus()

        val pageListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(DEFAULT_SIZE)
            .setPageSize(DEFAULT_SIZE)
            .build()

        videoUpdatePagedListResultLd =
            LivePagedListBuilder(videoUpdateDataFactory, pageListConfig).build()
    }

    fun refreshAllVideoUpdate() {
        videoUpdateDataFactory.refreshData()
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

    /* WATCH LATER */
    lateinit var watchLaterResultLd: MutableLiveData<Resource<Any>>

    fun watchLater(videoId: Int) {
        disposable.add(repository.watchLater(videoId).execute()
            .doOnSubscribe { watchLaterResultLd.setLoading() }
            .subscribe(
                { result -> watchLaterResultLd.setSuccess(result) },
                { throwable -> watchLaterResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun initVideoUpdateFragment() {
        followChannelResultLd = MutableLiveData()
        watchLaterResultLd = MutableLiveData()

        getAllVideo()
    }
}
