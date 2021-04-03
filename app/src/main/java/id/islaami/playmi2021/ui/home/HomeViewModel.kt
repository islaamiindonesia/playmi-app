package id.islaami.playmi2021.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import id.islaami.playmi2021.data.model.category.Category
import id.islaami.playmi2021.data.model.video.Video
import id.islaami.playmi2021.data.repository.CategoryRepository
import id.islaami.playmi2021.data.repository.ChannelRepository
import id.islaami.playmi2021.data.repository.PlaylistRepository
import id.islaami.playmi2021.data.repository.VideoRepository
import id.islaami.playmi2021.ui.base.BaseViewModel
import id.islaami.playmi2021.ui.datafactory.VideoByCategoryDataFactory
import id.islaami.playmi2021.ui.datafactory.VideoDataFactory
import id.islaami.playmi2021.util.*

class HomeViewModel(
    private val channel: ChannelRepository,
    private val video: VideoRepository,
    private val category: CategoryRepository,
    private val playlist: PlaylistRepository
) : BaseViewModel() {
    lateinit var networkStatusLd: LiveData<Resource<Unit>>

    /* CHANNEL */
    lateinit var hideChannelResultLd: MutableLiveData<Resource<Any>>
    lateinit var followChannelResultLd: MutableLiveData<Resource<Boolean>>
    lateinit var unfollowChannelResultLd: MutableLiveData<Resource<Boolean>>

    fun hideChannel(channelID: Int) {
        disposable.add(channel.hideChannel(channelID).execute()
            .doOnSubscribe { hideChannelResultLd.setLoading() }
            .subscribe(
                { result -> hideChannelResultLd.setSuccess(result) },
                { throwable -> hideChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun followChannel(channelID: Int) {
        disposable.add(channel.followChannel(channelID).execute()
            .doOnSubscribe { followChannelResultLd.setLoading() }
            .subscribe(
                { followChannelResultLd.setSuccess() },
                { throwable -> followChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun unfollowChannel(channelID: Int) {
        disposable.add(channel.unfollowChannel(channelID).execute()
            .doOnSubscribe { unfollowChannelResultLd.setLoading() }
            .subscribe(
                { unfollowChannelResultLd.setSuccess() },
                { throwable -> unfollowChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    /* VIDEO */
    lateinit var videoPagedListResultLd: LiveData<PagedList<Video>>
    lateinit var videoDataFactory: VideoDataFactory
    lateinit var videoByCategoryDataFactory: VideoByCategoryDataFactory

    fun getAllVideoByCategory(categoryId: Int) {
        videoByCategoryDataFactory = VideoByCategoryDataFactory(disposable, video, categoryId)

        networkStatusLd = videoByCategoryDataFactory.getNetworkStatus()

        val pageListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(DEFAULT_SIZE)
            .setPageSize(DEFAULT_SIZE)
            .build()

        videoPagedListResultLd =
            LivePagedListBuilder(videoByCategoryDataFactory, pageListConfig).build()
    }

    fun refreshAllVideoByCategory() {
        videoByCategoryDataFactory.refreshData()
    }

    fun getAllVideo() {
        videoDataFactory = VideoDataFactory(disposable, video)

        networkStatusLd = videoDataFactory.getNetworkStatus()

        val pageListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(DEFAULT_SIZE)
            .setPageSize(DEFAULT_SIZE)
            .build()

        videoPagedListResultLd = LivePagedListBuilder(videoDataFactory, pageListConfig).build()
    }

    fun refreshAllVideo() {
        videoDataFactory.refreshData()
    }

    /* CATEGORY */
    // declare live data object using lateinit, so that the initial value does not need to be assigned
    // or can be assigend later in some methods
    lateinit var getCategoryListResultLd: MutableLiveData<Resource<List<Category>>>

    fun getAllCategory() {
        disposable.add(category.getAllCategory().execute()
            .doOnSubscribe { getCategoryListResultLd.setLoading() } // do some process while subscribing
            .subscribe(
                // when subscribed, there will be 2 option, either data is successfully retreived or failed with a throwable error message.
                { result -> getCategoryListResultLd.setSuccess(result) },
                { throwable -> getCategoryListResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun initHome() {
        // assign the lateinit object
        getCategoryListResultLd = MutableLiveData()

        getAllCategory()
    }

    /* WATCH LATER */
    lateinit var watchLaterResultLd: MutableLiveData<Resource<Any>>

    fun watchLater(videoId: Int) {
        disposable.add(playlist.addLater(videoId).execute()
            .doOnSubscribe { watchLaterResultLd.setLoading() }
            .subscribe(
                { result -> watchLaterResultLd.setSuccess(result) },
                { throwable -> watchLaterResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun initVideoCategoryFragment(categoryId: Int) {
        hideChannelResultLd = MutableLiveData()
        followChannelResultLd = MutableLiveData()
        unfollowChannelResultLd = MutableLiveData()
        watchLaterResultLd = MutableLiveData()

        if (categoryId > 0) getAllVideoByCategory(categoryId)
        else getAllVideo()
    }
}
