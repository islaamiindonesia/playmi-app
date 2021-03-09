package id.islaami.playmi2021.ui.video

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import id.islaami.playmi2021.data.model.playlist.Playlist
import id.islaami.playmi2021.data.model.video.Video
import id.islaami.playmi2021.data.repository.ChannelRepository
import id.islaami.playmi2021.data.repository.PlaylistRepository
import id.islaami.playmi2021.data.repository.VideoRepository
import id.islaami.playmi2021.ui.base.BaseViewModel
import id.islaami.playmi2021.ui.datafactory.VideoByLabelDataFactory
import id.islaami.playmi2021.ui.datafactory.VideoBySubcategoryDataFactory
import id.islaami.playmi2021.ui.datafactory.VideoDataFactory
import id.islaami.playmi2021.util.*

class VideoViewModel(
    private val video: VideoRepository,
    private val channel: ChannelRepository,
    private val playlist: PlaylistRepository
) : BaseViewModel() {
    lateinit var networkStatusLd: LiveData<Resource<Unit>>

    /* VIDEO */
    lateinit var videoPagedListResultLd: LiveData<PagedList<Video>>
    lateinit var videoDataFactory: VideoDataFactory
    lateinit var videoBySubcategoryDataFactory: VideoBySubcategoryDataFactory
    lateinit var videoByLabelDataFactory: VideoByLabelDataFactory

    fun getAllVideo(query: String? = null) {
        videoDataFactory = VideoDataFactory(disposable, video, query)

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

    fun getAllVideoBySubcategory(categoryId: Int, subcategoryId: Int) {
        videoBySubcategoryDataFactory =
            VideoBySubcategoryDataFactory(disposable, video, categoryId, subcategoryId)

        networkStatusLd = videoBySubcategoryDataFactory.getNetworkStatus()

        val pageListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(DEFAULT_SIZE)
            .setPageSize(DEFAULT_SIZE)
            .build()

        videoPagedListResultLd =
            LivePagedListBuilder(videoBySubcategoryDataFactory, pageListConfig).build()
    }

    fun refreshAllVideoBySub() {
        videoBySubcategoryDataFactory.refreshData()
    }

    fun getAllVideoByLabel(categoryId: Int, subcategoryId: Int, labelId: Int) {
        videoByLabelDataFactory =
            VideoByLabelDataFactory(disposable, video, categoryId, subcategoryId, labelId)

        networkStatusLd = videoByLabelDataFactory.getNetworkStatus()

        val pageListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(DEFAULT_SIZE)
            .setPageSize(DEFAULT_SIZE)
            .build()

        videoPagedListResultLd =
            LivePagedListBuilder(videoByLabelDataFactory, pageListConfig).build()
    }

    fun refreshAllVideoByLabel() {
        videoByLabelDataFactory.refreshData()
    }

    /* WATCH LATER */
    lateinit var watchLaterResultLd: MutableLiveData<Resource<Any>>

    fun watchLater(videoId: Int) {
        disposable.add(video.watchLater(videoId).execute()
            .doOnSubscribe { watchLaterResultLd.setLoading() }
            .subscribe(
                { result -> watchLaterResultLd.setSuccess(result) },
                { throwable -> watchLaterResultLd.setError(throwable.getErrorMessage()) }
            ))
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

    fun createPlaylist(name: String, videoId: Int? = null) {
        disposable.add(playlist.create(name, videoId).execute()
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

    fun addToManyPlaylists(videoId: Int, playlistId: List<Int>) {
        disposable.add(playlist.addVideo(videoId, playlistId).execute()
            .doOnSubscribe { addToPlaylistResultLd.setLoading() }
            .subscribe(
                { result -> addToPlaylistResultLd.setSuccess(result) },
                { throwable -> addToPlaylistResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    /* VIDEO */
    lateinit var getVideoResultLd: MutableLiveData<Resource<Video>>

    fun getVideoDetail(id: Int) {
        disposable.add(video.getVideo(id).execute()
            .doOnSubscribe { getVideoResultLd.setLoading() }
            .subscribe(
                { result -> getVideoResultLd.setSuccess(result) },
                { throwable -> getVideoResultLd.setError(throwable.getErrorMessage()) }
            ))
    }


    /* CHANNEL */
    lateinit var getFollowingStatusResultLd: MutableLiveData<Resource<Boolean>>
    lateinit var hideResultLd: MutableLiveData<Resource<Any>>
    lateinit var followResultLd: MutableLiveData<Resource<Any>>
    lateinit var unfollowResultLd: MutableLiveData<Resource<Any>>

    fun hideChannel(channelID: Int) {
        disposable.add(channel.hideChannel(channelID).execute()
            .doOnSubscribe { hideResultLd.setLoading() }
            .subscribe(
                { hideResultLd.setSuccess() },
                { throwable -> hideResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun followChannel(id: Int) {
        disposable.add(channel.followChannel(id).execute()
            .doOnSubscribe { followResultLd.setLoading() }
            .subscribe(
                { followResultLd.setSuccess() },
                { throwable -> followResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun unfollowChannel(id: Int) {
        disposable.add(channel.unfollowChannel(id).execute()
            .doOnSubscribe { unfollowResultLd.setLoading() }
            .subscribe(
                { unfollowResultLd.setSuccess() },
                { throwable -> unfollowResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun initVideoDetailActivity(id: Int) {
        watchLaterResultLd = MutableLiveData()
        getFollowingStatusResultLd = MutableLiveData()
        getVideoResultLd = MutableLiveData()
        hideResultLd = MutableLiveData()
        followResultLd = MutableLiveData()
        unfollowResultLd = MutableLiveData()
        createPlaylistResultLd = MutableLiveData()
        addToPlaylistResultLd = MutableLiveData()
        getPlaylistsResultLd = MutableLiveData()

        getPlaylists()
        getVideoDetail(id)
    }

    fun initVideoSubcategoryActivity(categoryId: Int, subcategoryId: Int) {
        watchLaterResultLd = MutableLiveData()
        hideResultLd = MutableLiveData()
        followResultLd = MutableLiveData()
        unfollowResultLd = MutableLiveData()

        getAllVideoBySubcategory(categoryId, subcategoryId)
    }

    fun initVideoLabelActivity(categoryId: Int, subcategoryId: Int, labelId: Int) {
        watchLaterResultLd = MutableLiveData()
        hideResultLd = MutableLiveData()
        followResultLd = MutableLiveData()
        unfollowResultLd = MutableLiveData()

        getAllVideoByLabel(categoryId, subcategoryId, labelId)
    }

    fun initVideoSearchActivity() {
        watchLaterResultLd = MutableLiveData()
        hideResultLd = MutableLiveData()
        followResultLd = MutableLiveData()
        unfollowResultLd = MutableLiveData()
    }
}
