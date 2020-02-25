package com.example.playmi.ui.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.playmi.data.model.channel.Channel
import com.example.playmi.data.model.video.Video
import com.example.playmi.data.repository.ChannelRepository
import com.example.playmi.util.Resource
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