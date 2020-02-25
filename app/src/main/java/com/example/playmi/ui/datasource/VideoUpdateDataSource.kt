package com.example.playmi.ui.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.playmi.data.model.video.Video
import com.example.playmi.data.repository.VideoRepository
import com.example.playmi.util.*
import io.reactivex.disposables.CompositeDisposable

class VideoUpdateDataSource(
    private val disposable: CompositeDisposable,
    private val repository: VideoRepository,
    val networkStatus: MutableLiveData<Resource<Unit>> = MutableLiveData()
) : PageKeyedDataSource<Int, Video>() {
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Video>
    ) {
        networkStatus.setLoading()

        disposable.add(
            repository.getAllVideoByFollowing(1)
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
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Video>) {
        networkStatus.setLoading()

        disposable.add(
            repository.getAllVideoByFollowing(params.key)
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
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Video>) {

    }
}