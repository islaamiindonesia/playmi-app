package com.example.playmi.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.playmi.data.model.category.Category
import com.example.playmi.data.model.video.Video
import com.example.playmi.data.repository.CategoryRepository
import com.example.playmi.data.repository.HomeRepository
import com.example.playmi.data.repository.VideoRepository
import com.example.playmi.ui.base.BaseViewModel
import com.example.playmi.ui.datafactory.VideoDataFactory
import com.example.playmi.util.*

class HomeViewModel(
    private val home: HomeRepository,
    private val video: VideoRepository,
    private val category: CategoryRepository
) : BaseViewModel() {

    /* CHANNEL */
    lateinit var hideChannelResultLd: MutableLiveData<Resource<Any>>
    lateinit var followChannelResultLd: MutableLiveData<Resource<Any>>

    fun hideChannel(channelID: Int) {
        disposable.add(home.hideChannel(channelID).execute()
            .doOnSubscribe { hideChannelResultLd.setLoading() }
            .subscribe(
                { result -> hideChannelResultLd.setSuccess(result) },
                { throwable -> hideChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun followChannel(channelID: Int) {
        disposable.add(home.followChannel(channelID).execute()
            .doOnSubscribe { followChannelResultLd.setLoading() }
            .subscribe(
                { result -> followChannelResultLd.setSuccess(result) },
                { throwable -> followChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun unfollowChannel(channelID: Int) {
        disposable.add(home.unfollowChannel(channelID).execute()
            .doOnSubscribe { followChannelResultLd.setLoading() }
            .subscribe(
                { result -> followChannelResultLd.setSuccess(result) },
                { throwable -> followChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    /* VIDEO */
    lateinit var getVideoPagedListResultLd: LiveData<PagedList<Video>>
    lateinit var getVideoPagedListNetworkStatusLd: LiveData<Resource<Unit>>
    lateinit var videoDataFactory: VideoDataFactory

    fun getAllVideo(name: String? = null) {
        videoDataFactory = VideoDataFactory(disposable, video, name)

        getVideoPagedListNetworkStatusLd = videoDataFactory.getNetworkStatus()

        val pageListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(DEFAULT_SIZE)
            .setPageSize(DEFAULT_SIZE)
            .build()

        getVideoPagedListResultLd = LivePagedListBuilder(videoDataFactory, pageListConfig).build()
    }

    fun refreshAllVideo() {
        videoDataFactory.refreshData()
    }

    /* CATEGORY */
    lateinit var getCategoryListResultLd: MutableLiveData<Resource<List<Category>>>

    fun getAllCategory() {
        disposable.add(category.getAllCategory().execute()
            .doOnSubscribe { getCategoryListResultLd.setLoading() }
            .subscribe(
                { result -> getCategoryListResultLd.setSuccess(result) },
                { throwable -> getCategoryListResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun initHome() {
        getCategoryListResultLd = MutableLiveData()

        getAllVideo()
        getAllCategory()
    }

    /* WATCH LATER */
    lateinit var watchLaterResultLd: MutableLiveData<Resource<Any>>

    fun watchLater(videoId: Int) {
        disposable.add(home.watchLater(videoId).execute()
            .doOnSubscribe { watchLaterResultLd.setLoading() }
            .subscribe(
                { result -> watchLaterResultLd.setSuccess(result) },
                { throwable -> watchLaterResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun initCustomFragment(categoryName: String? = null) {
        hideChannelResultLd = MutableLiveData()
        followChannelResultLd = MutableLiveData()
        watchLaterResultLd = MutableLiveData()

        if (categoryName.toString().toLowerCase() == "semua") {
            getAllVideo()
        } else {
            getAllVideo(categoryName)
        }
    }
}
