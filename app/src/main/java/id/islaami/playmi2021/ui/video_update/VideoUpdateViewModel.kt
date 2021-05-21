package id.islaami.playmi2021.ui.video_update

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import id.islaami.playmi2021.data.model.video.Video
import id.islaami.playmi2021.data.repository.VideoRepository
import id.islaami.playmi2021.ui.base.BaseViewModel
import id.islaami.playmi2021.ui.datafactory.VideoUpdateDataFactory
import id.islaami.playmi2021.util.*

class VideoUpdateViewModel(
    private val repository: VideoRepository
) : BaseViewModel() {
    lateinit var networkStatusLd: LiveData<Resource<Unit>>

    /* VIDEO */
    lateinit var videoUpdatePagedListResultLd: LiveData<PagedList<Video>>
    lateinit var videoUpdateDataFactory: VideoUpdateDataFactory

    // shuffle 1 of 0
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

    fun changeParam(shuffle: Int) {
        videoUpdateDataFactory.setShuffle(shuffle)
    }

    fun refreshAllVideo() {
        videoUpdateDataFactory.refreshData()
    }

    /* FOLLOW UNFOLLOW */
    lateinit var followChannelResultLd: MutableLiveData<Resource<Boolean>>
    lateinit var unfollowChannelResultLd: MutableLiveData<Resource<Boolean>>

    fun followChannel(id: Int) {
        disposable.add(repository.followChannel(id).execute()
            .doOnSubscribe { followChannelResultLd.setLoading() }
            .subscribe(
                { followChannelResultLd.setSuccess() },
                { throwable -> followChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun unfollowChannel(channelID: Int) {
        disposable.add(repository.unfollowChannel(channelID).execute()
            .doOnSubscribe { unfollowChannelResultLd.setLoading() }
            .subscribe(
                { unfollowChannelResultLd.setSuccess() },
                { throwable -> unfollowChannelResultLd.setError(throwable.getErrorMessage()) }
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
        unfollowChannelResultLd = MutableLiveData()
        watchLaterResultLd = MutableLiveData()

        getAllVideo()
    }
}
