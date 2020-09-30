package id.islaami.playmi.ui.channel_following

import androidx.lifecycle.MutableLiveData
import id.islaami.playmi.data.model.channel.Channel
import id.islaami.playmi.data.repository.ChannelRepository
import id.islaami.playmi.ui.base.BaseViewModel
import id.islaami.playmi.util.*

class OrganizeChannelViewModel(private val repository: ChannelRepository) : BaseViewModel() {

    /* CHANNEL */
    lateinit var channelFollowingLd: MutableLiveData<Resource<List<Channel>>>
    lateinit var getChannelHiddenResultLd: MutableLiveData<Resource<List<Channel>>>

    fun getChannelFollow(query: String? = null) {
        disposable.add(repository.getChannelFollow(query).execute()
            .doOnSubscribe { channelFollowingLd.setLoading() }
            .subscribe(
                { result -> channelFollowingLd.setSuccess(result) },
                { throwable -> channelFollowingLd.setError(throwable.getErrorMessage()) }
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
    lateinit var followingResultLd: MutableLiveData<Resource<Any>>
    lateinit var channelStatusResultLd: MutableLiveData<Resource<Any>>

    fun followChannel(id: Int) {
        disposable.add(repository.followChannel(id).execute()
            .doOnSubscribe { followingResultLd.setLoading() }
            .subscribe(
                { followingResultLd.setSuccess() },
                { throwable -> followingResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun unfollowChannel(id: Int) {
        disposable.add(repository.unfollowChannel(id).execute()
            .doOnSubscribe { followingResultLd.setLoading() }
            .subscribe(
                { followingResultLd.setSuccess() },
                { throwable -> followingResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun hideChannel(channelID: Int) {
        disposable.add(repository.hideChannel(channelID).execute()
            .doOnSubscribe { channelStatusResultLd.setLoading() }
            .subscribe(
                { channelStatusResultLd.setSuccess() },
                { throwable -> channelStatusResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun showChannel(channelID: Int) {
        disposable.add(repository.showChannel(channelID).execute()
            .doOnSubscribe { channelStatusResultLd.setLoading() }
            .subscribe(
                { channelStatusResultLd.setSuccess() },
                { throwable -> channelStatusResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun initFollowingFragment() {
        followingResultLd = MutableLiveData()
        channelFollowingLd = MutableLiveData()

        getChannelFollow()
    }

    fun initHiddenFragment() {
        channelStatusResultLd = MutableLiveData()
        getChannelHiddenResultLd = MutableLiveData()

        getChannelHidden()
    }
}
