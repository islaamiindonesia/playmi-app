package com.example.playmi.ui.datafactory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import com.example.playmi.data.model.channel.Channel
import com.example.playmi.data.model.video.Video
import com.example.playmi.data.repository.ChannelRepository
import com.example.playmi.data.repository.VideoRepository
import com.example.playmi.ui.datasource.ChannelFollowDataSource
import com.example.playmi.ui.datasource.VideoDataSource
import com.example.playmi.util.Resource
import io.reactivex.disposables.CompositeDisposable

class ChannelFollowDataFactory(
    private val disposable: CompositeDisposable,
    private val repository: ChannelRepository,
    val mutableLiveData: MutableLiveData<ChannelFollowDataSource> = MutableLiveData()
) : DataSource.Factory<Int, Channel>() {

    override fun create(): DataSource<Int, Channel> {
        val dataSource = ChannelFollowDataSource(disposable, repository)
        mutableLiveData.postValue(dataSource)
        return dataSource
    }

    fun getNetworkStatus(): LiveData<Resource<Unit>> =
        Transformations.switchMap(mutableLiveData) { dataSource -> dataSource.networkStatus }

    fun refreshData() {
        mutableLiveData.value?.invalidate()
    }
}