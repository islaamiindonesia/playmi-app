package com.example.playmi.ui.following

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.playmi.data.model.video.Video
import com.example.playmi.data.repository.ChannelRepository
import com.example.playmi.ui.base.BaseViewModel
import com.example.playmi.ui.datafactory.ChannelFollowDataFactory
import com.example.playmi.ui.datafactory.VideoDataFactory
import com.example.playmi.util.DEFAULT_SIZE
import com.example.playmi.util.Resource

class FollowingViewModel(private val repository: ChannelRepository) : BaseViewModel() {

    /* CHANNEL */
    lateinit var getChannelPagedListResultLd: LiveData<PagedList<Video>>
    lateinit var getChannelPagedListNetworkStatusLd: LiveData<Resource<Unit>>
    lateinit var videoDataFactory: VideoDataFactory

    fun getAllChannel(name: String? = null) {
//        videoDataFactory = VideoDataFactory(disposable, video, name)

        getChannelPagedListNetworkStatusLd = videoDataFactory.getNetworkStatus()

        val pageListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(DEFAULT_SIZE)
            .setPageSize(DEFAULT_SIZE)
            .build()

        getChannelPagedListResultLd = LivePagedListBuilder(videoDataFactory, pageListConfig).build()
    }

    fun refreshAllVideo() {
        videoDataFactory.refreshData()
    }

    fun initFollowingFragment() {
        /*getCategoryListResultLd = MutableLiveData()

        getAllVideo()
        getAllCategory()*/
    }
}
