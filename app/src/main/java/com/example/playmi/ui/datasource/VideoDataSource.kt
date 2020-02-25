package com.example.playmi.ui.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.playmi.data.model.video.Video
import com.example.playmi.data.repository.VideoRepository
import com.example.playmi.util.*
import io.reactivex.disposables.CompositeDisposable

class VideoDataSource(
    private val disposable: CompositeDisposable,
    private val repository: VideoRepository,
    private var categoryName: String? = null,
    val networkStatus: MutableLiveData<Resource<Unit>> = MutableLiveData()
) : PageKeyedDataSource<Int, Video>() {
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Video>
    ) {
        networkStatus.setLoading()

        disposable.add(
            repository.getAllVideo(1, category = categoryName)
                .subscribe(
                    { result ->
                        if (result != null) {
                            if (!result.videos.isNullOrEmpty()) {
                                callback.onResult(result.videos, null, 2)
                            }
                        }
                        networkStatus.setSuccess()
                    },
                    { throwable -> networkStatus.setError(throwable.getErrorMessage()) })
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Video>) {
        networkStatus.setLoading()

        disposable.add(
            repository.getAllVideo(params.key, category = categoryName)
                .subscribe(
                    { result ->
                        if (result != null) {
                            if (!result.videos.isNullOrEmpty()) {
                                callback.onResult(result.videos, params.key + 1)
                            }
                        }
                        networkStatus.setSuccess()
                    },
                    { throwable -> networkStatus.setError(throwable.getErrorMessage()) })
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Video>) {

    }
}