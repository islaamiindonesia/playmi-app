package com.example.playmi.ui.datafactory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import com.example.playmi.data.model.video.Video
import com.example.playmi.data.repository.VideoRepository
import com.example.playmi.ui.datasource.VideoDataSource
import com.example.playmi.util.Resource
import io.reactivex.disposables.CompositeDisposable

class VideoDataFactory(
    private val disposable: CompositeDisposable,
    private val repository: VideoRepository,
    private var categoryName: String? = null,
    val mutableLiveData: MutableLiveData<VideoDataSource> = MutableLiveData()
) : DataSource.Factory<Int, Video>() {

    override fun create(): DataSource<Int, Video> {
        val dataSource = VideoDataSource(disposable, repository, categoryName)
        mutableLiveData.postValue(dataSource)
        return dataSource
    }

    fun getNetworkStatus(): LiveData<Resource<Unit>> =
        Transformations.switchMap(mutableLiveData) { dataSource -> dataSource.networkStatus }

    fun refreshData() {
        mutableLiveData.value?.invalidate()
    }
}