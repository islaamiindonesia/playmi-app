package id.islaami.playmi2021.ui.datafactory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import id.islaami.playmi2021.data.model.channel.Channel
import id.islaami.playmi2021.data.repository.ChannelRepository
import id.islaami.playmi2021.ui.datasource.ChannelFollowDataSource
import id.islaami.playmi2021.util.Resource
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