package id.islaami.playmi.ui.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import id.islaami.playmi.data.model.video.Video
import id.islaami.playmi.data.repository.VideoRepository
import id.islaami.playmi.util.*
import io.reactivex.disposables.CompositeDisposable

class VideoByLabelDataSource(
    private val disposable: CompositeDisposable,
    private val repository: VideoRepository,
    private var categoryId: Int,
    private var subcategoryId: Int,
    private var labelId: Int,
    val networkStatus: MutableLiveData<Resource<Unit>> = MutableLiveData()
) : PageKeyedDataSource<Int, Video>() {
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Video>
    ) {
        networkStatus.setLoading()

        disposable.add(
            repository.getAllVideoByLabel(1, categoryId, subcategoryId, labelId)
                .subscribe(
                    { result ->
                        if (!result.isNullOrEmpty()) {
                            callback.onResult(result, null, 2)
                            networkStatus.setSuccess()
                        } else {
                            networkStatus.setError(ERROR_EMPTY_LIST)
                        }
                    },
                    { throwable -> networkStatus.setError(throwable.getErrorMessage()) })
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Video>) {
        networkStatus.setLoading()

        disposable.add(
            repository.getAllVideoByLabel(params.key, categoryId, subcategoryId, labelId)
                .subscribe(
                    { result ->
                        if (!result.isNullOrEmpty()) {
                            callback.onResult(result, params.key + 1)
                        }
                        networkStatus.setSuccess()
                    },
                    { throwable -> networkStatus.setError(throwable.getErrorMessage()) })
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Video>) {

    }
}