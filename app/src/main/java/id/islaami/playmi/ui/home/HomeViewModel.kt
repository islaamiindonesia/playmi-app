package id.islaami.playmi.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import id.islaami.playmi.data.model.category.Category
import id.islaami.playmi.data.model.playlist.Playlist
import id.islaami.playmi.data.model.video.Video
import id.islaami.playmi.data.repository.CategoryRepository
import id.islaami.playmi.data.repository.ChannelRepository
import id.islaami.playmi.data.repository.PlaylistRepository
import id.islaami.playmi.data.repository.VideoRepository
import id.islaami.playmi.ui.base.BaseViewModel
import id.islaami.playmi.ui.datafactory.VideoByCategoryDataFactory
import id.islaami.playmi.ui.datafactory.VideoDataFactory
import id.islaami.playmi.util.*

class HomeViewModel(
    private val channel: ChannelRepository,
    private val video: VideoRepository,
    private val category: CategoryRepository,
    private val playlist: PlaylistRepository
) : BaseViewModel() {
    lateinit var pagedListNetworkStatusLd: LiveData<Resource<Unit>>

    /* CHANNEL */
    lateinit var hideChannelResultLd: MutableLiveData<Resource<Any>>
    lateinit var followChannelResultLd: MutableLiveData<Resource<Boolean>>

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
            .doOnSubscribe { followChannelResultLd.setLoading() }
            .subscribe(
                {followChannelResultLd.setSuccess() },
                { throwable -> followChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    /* VIDEO */
    lateinit var videoPagedListResultLd: LiveData<PagedList<Video>>
    lateinit var videoDataFactory: VideoDataFactory
    lateinit var videoByCategoryDataFactory: VideoByCategoryDataFactory

    fun getAllVideoByCategory(categoryId: Int) {
        videoByCategoryDataFactory = VideoByCategoryDataFactory(disposable, video, categoryId)

        pagedListNetworkStatusLd = videoByCategoryDataFactory.getNetworkStatus()

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

        pagedListNetworkStatusLd = videoDataFactory.getNetworkStatus()

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

    fun createPlaylist(name: String) {
        disposable.add(playlist.create(name).execute()
            .doOnSubscribe { createPlaylistResultLd.setLoading() }
            .subscribe(
                { result -> createPlaylistResultLd.setSuccess(result) },
                { throwable -> createPlaylistResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun addToPlaylist(videoId: Int, playlistId: Int) {
        disposable.add(playlist.addVideo(videoId, playlistId).execute()
            .doOnSubscribe { addToPlaylistResultLd.setLoading() }
            .subscribe(
                { result -> addToPlaylistResultLd.setSuccess(result) },
                { throwable -> addToPlaylistResultLd.setError(throwable.getErrorMessage()) }
            ))
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
        watchLaterResultLd = MutableLiveData()
        getPlaylistsResultLd = MutableLiveData()

        if (categoryId > 0) getAllVideoByCategory(categoryId)
        else getAllVideo()
    }
}
