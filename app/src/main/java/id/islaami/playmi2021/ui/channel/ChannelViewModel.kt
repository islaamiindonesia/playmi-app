package id.islaami.playmi2021.ui.channel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import id.islaami.playmi2021.data.model.channel.Channel
import id.islaami.playmi2021.data.model.video.Video
import id.islaami.playmi2021.data.repository.ChannelRepository
import id.islaami.playmi2021.data.repository.PlaylistRepository
import id.islaami.playmi2021.data.repository.VideoRepository
import id.islaami.playmi2021.ui.base.BaseViewModel
import id.islaami.playmi2021.ui.datafactory.VideoByChannelDataFactory
import id.islaami.playmi2021.util.*

class ChannelViewModel(
    val repository: ChannelRepository,
    val video: VideoRepository,
    val playlist: PlaylistRepository
) : BaseViewModel() {
    lateinit var pagedListNetworkStatusLd: LiveData<Resource<Unit>>

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

    /* VIDEO */
    lateinit var videoPagedListResultLd: LiveData<PagedList<Video>>
    lateinit var videoFactory: VideoByChannelDataFactory

    fun getAllVideo(id: Int) {
        videoFactory = VideoByChannelDataFactory(disposable, video, id)

        pagedListNetworkStatusLd = videoFactory.getNetworkStatus()

        val pageListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(DEFAULT_SIZE)
            .setPageSize(DEFAULT_SIZE)
            .build()

        videoPagedListResultLd = LivePagedListBuilder(videoFactory, pageListConfig).build()
    }

    fun refreshAllVideo() {
        videoFactory.refreshData()
    }

    /* CHANNEL */
    lateinit var channelDetailResultLd: MutableLiveData<Resource<Channel>>
    lateinit var showChannelResultLd: MutableLiveData<Resource<Any>>
    lateinit var hideChannelResultLd: MutableLiveData<Resource<Any>>
    lateinit var followChannelResultLd: MutableLiveData<Resource<Boolean>>
    lateinit var unfollowChannelResultLd: MutableLiveData<Resource<Boolean>>

    fun getChannelDetail(channelID: Int) {
        disposable.add(repository.getDetailChannel(channelID).execute()
            .doOnSubscribe { channelDetailResultLd.setLoading() }
            .subscribe(
                { result -> channelDetailResultLd.setSuccess(result) },
                { throwable -> channelDetailResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun showChannel(channelID: Int) {
        disposable.add(repository.showChannel(channelID).execute()
            .doOnSubscribe { showChannelResultLd.setLoading() }
            .subscribe(
                { showChannelResultLd.setSuccess() },
                { throwable -> showChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun hideChannel(channelID: Int) {
        disposable.add(repository.hideChannel(channelID).execute()
            .doOnSubscribe { hideChannelResultLd.setLoading() }
            .subscribe(
                { result -> hideChannelResultLd.setSuccess(result) },
                { throwable -> hideChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun followChannel(channelID: Int) {
        disposable.add(repository.followChannel(channelID).execute()
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

    fun initChannelDetail(channelID: Int) {
        channelDetailResultLd = MutableLiveData()
        showChannelResultLd = MutableLiveData()
        hideChannelResultLd = MutableLiveData()
        followChannelResultLd = MutableLiveData()
        unfollowChannelResultLd = MutableLiveData()

        getChannelDetail(channelID)
    }

    fun initChannelVideo(channelID: Int) {
        watchLaterResultLd = MutableLiveData()

        getAllVideo(channelID)
    }
}
