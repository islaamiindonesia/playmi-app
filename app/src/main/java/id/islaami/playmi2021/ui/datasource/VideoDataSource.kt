package id.islaami.playmi2021.ui.datasource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import id.islaami.playmi2021.data.model.video.Video
import id.islaami.playmi2021.data.repository.VideoRepository
import id.islaami.playmi2021.util.*
import io.reactivex.disposables.CompositeDisposable
import okhttp3.internal.toImmutableList

class VideoDataSource(
    private val disposable: CompositeDisposable,
    private val repository: VideoRepository,
    var query: String? = null,
    val networkStatus: MutableLiveData<Resource<Unit>> = MutableLiveData()
) : PageKeyedDataSource<Int, Video>() {
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Video>
    ) {
        networkStatus.setLoading()

        disposable.add(
            repository.getAllVideo(1, query)
                .subscribe(
                    { result ->
                        if (!result.isNullOrEmpty()) {
                            val resultWithAds: MutableList<Video?> =
                                result.filter { it.isPublished ?: false }.toMutableList()
                            result.forEachIndexed { index, video ->
                                if ((index + 1) % 6 == 0 && (index + 1) != 1)
                                    resultWithAds.add(index+1, Video(99990000+index, "ads"))
                            }
                            callback.onResult(resultWithAds, null, 2)
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
            repository.getAllVideo(params.key, query)
                .subscribe(
                    { result ->
                        if (!result.isNullOrEmpty()) {
                            val resultWithAds: MutableList<Video?> =
                                result.filter { it.isPublished ?: false }.toMutableList()
                            val previousCount = (params.key-1)*10
                            val previousCountWithAds = previousCount + (previousCount/6)
                            result.forEachIndexed { index, video ->
                                val indexWithPrevious = index+previousCountWithAds
                                if ((indexWithPrevious + 1) % 7 == 0)
                                    resultWithAds.add(index+1, Video(99990000+indexWithPrevious, "ads"))
                            }
                            callback.onResult(resultWithAds, params.key + 1)
                        }
                        networkStatus.setSuccess()
                    },
                    { throwable -> networkStatus.setError(throwable.getErrorMessage()) })
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Video>) {

    }
}