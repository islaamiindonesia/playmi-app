package com.example.playmi.ui.datafactory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import com.example.playmi.data.model.video.Video
import com.example.playmi.data.repository.VideoRepository
import com.example.playmi.ui.datasource.VideoUpdateDataSource
import com.example.playmi.util.Resource
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Kemal Amru Ramadhan on 05/08/2019.
 */
class VideoUpdateDataFactory(
    private val disposable: CompositeDisposable,
    private val repository: VideoRepository,
    val mutableLiveData: MutableLiveData<VideoUpdateDataSource> = MutableLiveData()
) : DataSource.Factory<Int, Video>() {

    override fun create(): DataSource<Int, Video> {
        val dataSource = VideoUpdateDataSource(disposable, repository)
        mutableLiveData.postValue(dataSource)
        return dataSource
    }

    fun getNetworkStatus(): LiveData<Resource<Unit>> =
        Transformations.switchMap(mutableLiveData) { dataSource -> dataSource.networkStatus }

    fun refreshData() {
        mutableLiveData.value?.invalidate()
    }
}