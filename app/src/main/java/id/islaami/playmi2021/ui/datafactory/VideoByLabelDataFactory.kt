package id.islaami.playmi2021.ui.datafactory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import id.islaami.playmi2021.data.model.video.Video
import id.islaami.playmi2021.data.repository.VideoRepository
import id.islaami.playmi2021.ui.datasource.VideoByLabelDataSource
import id.islaami.playmi2021.util.Resource
import io.reactivex.disposables.CompositeDisposable

class VideoByLabelDataFactory(
    private val disposable: CompositeDisposable,
    private val repository: VideoRepository,
    private var categoryId: Int,
    private var subcategoryId: Int,
    private var labelId: Int,
    val mutableLiveData: MutableLiveData<VideoByLabelDataSource> = MutableLiveData()
) : DataSource.Factory<Int, Video>() {

    override fun create(): DataSource<Int, Video> {
        val dataSource =
            VideoByLabelDataSource(disposable, repository, categoryId, subcategoryId, labelId)
        mutableLiveData.postValue(dataSource)
        return dataSource
    }

    fun getNetworkStatus(): LiveData<Resource<Unit>> =
        Transformations.switchMap(mutableLiveData) { dataSource -> dataSource.networkStatus }

    fun refreshData() {
        mutableLiveData.value?.invalidate()
    }
}