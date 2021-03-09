package id.islaami.playmi2021.ui.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import id.islaami.playmi2021.data.model.video.Video
import id.islaami.playmi2021.data.repository.VideoRepository
import id.islaami.playmi2021.util.*
import io.reactivex.disposables.CompositeDisposable

class VideoByChannelDataSource(
    private val disposable: CompositeDisposable,
    private val repository: VideoRepository,
    private var channelId: Int,
    val networkStatus: MutableLiveData<Resource<Unit>> = MutableLiveData()
) : PageKeyedDataSource<Int, Video>() {
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Video>
    ) {
        networkStatus.setLoading()

        disposable.add(
            repository.getAllVideoByChannel(1, channelId)
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
            repository.getAllVideoByChannel(params.key, channelId)
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