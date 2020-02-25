package com.example.playmi.ui.channel

import androidx.lifecycle.MutableLiveData
import com.example.playmi.data.model.channel.Channel
import com.example.playmi.data.model.video.Video
import com.example.playmi.data.repository.ChannelRepository
import com.example.playmi.ui.base.BaseViewModel
import com.example.playmi.util.*

class ChannelViewModel(private val repository: ChannelRepository) : BaseViewModel() {
    /* CHANNEL */
    lateinit var channelDetailResultLd: MutableLiveData<Resource<Channel>>
    lateinit var channelVideosResultLd: MutableLiveData<Resource<List<Video>>>
    lateinit var hideStatusResultLd: MutableLiveData<Resource<Boolean>>
    lateinit var followStatusResultLd: MutableLiveData<Resource<Boolean>>
    lateinit var hideChannelResultLd: MutableLiveData<Resource<Any>>
    lateinit var followChannelResultLd: MutableLiveData<Resource<Any>>

    fun getChannelDetail(channelID: Int) {
        disposable.add(repository.getDetailChannel(channelID).execute()
            .doOnSubscribe { channelDetailResultLd.setLoading() }
            .subscribe(
                { result -> channelDetailResultLd.setSuccess(result) },
                { throwable -> channelDetailResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun getChannelVideos(channelID: Int) {
        disposable.add(repository.getChannelVideos(channelID).execute()
            .doOnSubscribe { channelVideosResultLd.setLoading() }
            .subscribe(
                { result -> channelVideosResultLd.setSuccess(result) },
                { throwable -> channelVideosResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun getFollowStatus(channelID: Int) {
        disposable.add(repository.getFollowingStatus(channelID).execute()
            .doOnSubscribe { followStatusResultLd.setLoading() }
            .subscribe(
                { result -> followStatusResultLd.setSuccess(result) },
                { throwable -> followStatusResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun getHideStatus(channelID: Int) {
        disposable.add(repository.getHideStatus(channelID).execute()
            .doOnSubscribe { hideStatusResultLd.setLoading() }
            .subscribe(
                { result -> hideStatusResultLd.setSuccess(result) },
                { throwable -> hideStatusResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun hideChannel(channelID: Int) {
        disposable.add(repository.hideChannel(channelID).execute()
            .doOnSubscribe { hideChannelResultLd.setLoading() }
            .subscribe(
                { result -> hideChannelResultLd.setSuccess(result) },
                { throwable -> hideChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun followChannel(channelID: Int) {
        disposable.add(repository.followChannel(channelID).execute()
            .doOnSubscribe { followChannelResultLd.setLoading() }
            .subscribe(
                { result -> followChannelResultLd.setSuccess(result) },
                { throwable -> followChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun unfollowChannel(channelID: Int) {
        disposable.add(repository.unfollowChannel(channelID).execute()
            .doOnSubscribe { followChannelResultLd.setLoading() }
            .subscribe(
                { result -> followChannelResultLd.setSuccess(result) },
                { throwable -> followChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun initChannelDetail(channelID: Int) {
        channelDetailResultLd = MutableLiveData()
        channelVideosResultLd = MutableLiveData()
        hideStatusResultLd = MutableLiveData()
        followStatusResultLd = MutableLiveData()
        hideChannelResultLd = MutableLiveData()
        followChannelResultLd = MutableLiveData()

        getChannelDetail(channelID)
        getChannelVideos(channelID)
        getFollowStatus(channelID)
        getHideStatus(channelID)
    }
}
