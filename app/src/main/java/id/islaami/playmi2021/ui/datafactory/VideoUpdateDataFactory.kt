package id.islaami.playmi2021.ui.datafactory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import id.islaami.playmi2021.data.model.video.Video
import id.islaami.playmi2021.data.repository.VideoRepository
import id.islaami.playmi2021.ui.datasource.VideoUpdateDataSource
import id.islaami.playmi2021.util.Resource
import io.reactivex.disposables.CompositeDisposable

class VideoUpdateDataFactory(
    private val disposable: CompositeDisposable,
    private val repository: VideoRepository,
    val mutableLiveData: MutableLiveData<VideoUpdateDataSource> = MutableLiveData()
) : DataSource.Factory<Int, Video>() {
    private var shuffle = 0

    override fun create(): DataSource<Int, Video> {
        val dataSource = VideoUpdateDataSource(disposable, repository, shuffle)
        mutableLiveData.postValue(dataSource)
        return dataSource
    }

    fun getNetworkStatus(): LiveData<Resource<Unit>> =
        Transformations.switchMap(mutableLiveData) { dataSource -> dataSource.networkStatus }

    fun setShuffle(shuffle: Int) {
        this.shuffle = shuffle
        refreshData()
    }

    fun refreshData() {
        mutableLiveData.value?.invalidate()
    }
}