package id.islaami.playmi2021.ui.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import id.islaami.playmi2021.data.model.channel.Channel
import id.islaami.playmi2021.data.repository.ChannelRepository
import id.islaami.playmi2021.util.Resource
import io.reactivex.disposables.CompositeDisposable

class ChannelFollowDataSource(
    private val disposable: CompositeDisposable,
    private val repository: ChannelRepository,
    val networkStatus: MutableLiveData<Resource<Unit>> = MutableLiveData()
) : PageKeyedDataSource<Int, Channel>() {
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Channel>
    ) {
        /*networkStatus.setLoading()

        disposable.add(
            repository.getAllVideo(1, categoryName)
                .subscribe(
                    { result ->
                        if (result != null) {
                            if (!result.videos.isNullOrEmpty()) {
                                callback.onResult(result.videos, null, 2)
                                networkStatus.setSuccess()
                            } else {
                                networkStatus.setError(ERROR_EMPTY_LIST)
                            }
                        } else {
                            networkStatus.setError(ERROR_EMPTY_LIST)
                        }
                    },
                    { throwable -> networkStatus.setError(throwable.getErrorMessage()) })
        )*/
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Channel>) {
        /*networkStatus.setLoading()

        disposable.add(
            repository.getAllVideo(params.key, categoryName)
                .subscribe(
                    { result ->
                        if (result != null) {
                            if (!result.videos.isNullOrEmpty()) {
                                callback.onResult(result.videos, params.key + 1)
                                networkStatus.setSuccess()
                            }
                        }
                    },
                    { throwable -> networkStatus.setError(throwable.getErrorMessage()) })
        )*/
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Channel>) {

    }
}