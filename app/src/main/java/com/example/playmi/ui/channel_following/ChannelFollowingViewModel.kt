package com.example.playmi.ui.channel_following

import androidx.lifecycle.MutableLiveData
import com.example.playmi.data.model.channel.Channel
import com.example.playmi.data.repository.ChannelRepository
import com.example.playmi.ui.base.BaseViewModel
import com.example.playmi.util.*

class ChannelFollowingViewModel(private val repository: ChannelRepository) : BaseViewModel() {

    /* CHANNEL */
    lateinit var getChannelFollowResultLd: MutableLiveData<Resource<List<Channel>>>
    lateinit var getChannelHiddenResultLd: MutableLiveData<Resource<List<Channel>>>

    fun getChannelFollow() {
        disposable.add(repository.getChannelFollow().execute()
            .doOnSubscribe { getChannelFollowResultLd.setLoading() }
            .subscribe(
                { result -> getChannelFollowResultLd.setSuccess(result) },
                { throwable -> getChannelFollowResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun getChannelHidden() {
        disposable.add(repository.getChannelHidden().execute()
            .doOnSubscribe { getChannelHiddenResultLd.setLoading() }
            .subscribe(
                { result -> getChannelHiddenResultLd.setSuccess(result) },
                { throwable -> getChannelHiddenResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    /* FOLLOW UNFOLLOW */
    lateinit var followChannelResultLd: MutableLiveData<Resource<Any>>
    lateinit var hideChannelResultLd: MutableLiveData<Resource<Any>>

    fun followChannel(id: Int) {
        disposable.add(repository.followChannel(id).execute()
            .doOnSubscribe { followChannelResultLd.setLoading() }
            .subscribe(
                { followChannelResultLd.setSuccess() },
                { throwable -> followChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun unfollowChannel(id: Int) {
        disposable.add(repository.unfollowChannel(id).execute()
            .doOnSubscribe { followChannelResultLd.setLoading() }
            .subscribe(
                { followChannelResultLd.setSuccess() },
                { throwable -> followChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun hideChannel(channelID: Int) {
        disposable.add(repository.hideChannel(channelID).execute()
            .doOnSubscribe { hideChannelResultLd.setLoading() }
            .subscribe(
                { hideChannelResultLd.setSuccess() },
                { throwable -> hideChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun showChannel(channelID: Int) {
        disposable.add(repository.showChannel(channelID).execute()
            .doOnSubscribe { hideChannelResultLd.setLoading() }
            .subscribe(
                { hideChannelResultLd.setSuccess() },
                { throwable -> hideChannelResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun initFollowingFragment() {
        followChannelResultLd = MutableLiveData()
        hideChannelResultLd = MutableLiveData()
        getChannelFollowResultLd = MutableLiveData()

        getChannelFollow()
    }

    fun initHiddenFragment() {
        hideChannelResultLd = MutableLiveData()
        getChannelHiddenResultLd = MutableLiveData()

        getChannelHidden()
    }
}
