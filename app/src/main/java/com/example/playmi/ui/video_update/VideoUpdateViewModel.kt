package com.example.playmi.ui.video_update

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.playmi.data.model.video.Video
import com.example.playmi.data.repository.VideoRepository
import com.example.playmi.ui.base.BaseViewModel
import com.example.playmi.ui.datafactory.VideoUpdateDataFactory
import com.example.playmi.util.*

class VideoUpdateViewModel(
    private val repository: VideoRepository
) : BaseViewModel() {

    /* VIDEO */
    lateinit var getVideoUpdatePagedListResultLd: LiveData<PagedList<Video>>
    lateinit var getVideoUpdateNetworkStatusLd: LiveData<Resource<Unit>>
    lateinit var videoUpdateDataFactory: VideoUpdateDataFactory

    fun getAllVideoUpdate() {
        videoUpdateDataFactory = VideoUpdateDataFactory(disposable, repository)

        getVideoUpdateNetworkStatusLd = videoUpdateDataFactory.getNetworkStatus()

        val pageListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(DEFAULT_SIZE)
            .setPageSize(DEFAULT_SIZE)
            .build()

        getVideoUpdatePagedListResultLd =
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
        getAllVideoUpdate()
    }
}
