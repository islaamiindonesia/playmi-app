package com.example.playmi.ui.video_update

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.playmi.data.model.video.Video
import com.example.playmi.data.repository.VideoRepository
import com.example.playmi.ui.base.BaseViewModel
import com.example.playmi.ui.datafactory.VideoUpdateDataFactory
import com.example.playmi.util.DEFAULT_SIZE
import com.example.playmi.util.Resource

class VideoUpdateViewModel(private val repository: VideoRepository) : BaseViewModel() {

    /* VIDEO */
    lateinit var getVideoPagedListResultLd: LiveData<PagedList<Video>>
    lateinit var getVideoPagedListNetworkStatusLd: LiveData<Resource<Unit>>
    lateinit var videoUpdateDataFactory: VideoUpdateDataFactory

    fun getAllVideo() {
        videoUpdateDataFactory = VideoUpdateDataFactory(disposable, repository)

        getVideoPagedListNetworkStatusLd = videoUpdateDataFactory.getNetworkStatus()

        val pageListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(DEFAULT_SIZE)
            .setPageSize(DEFAULT_SIZE)
            .build()

        getVideoPagedListResultLd =
            LivePagedListBuilder(videoUpdateDataFactory, pageListConfig).build()
    }

    fun refreshAllVideo() {
        videoUpdateDataFactory.refreshData()
    }

    fun initFollowingFragment() {
        getAllVideo()
    }
}
