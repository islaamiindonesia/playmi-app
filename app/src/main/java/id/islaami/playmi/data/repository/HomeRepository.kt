package id.islaami.playmi.data.repository

import id.islaami.playmi.data.api.HomeApi
import id.islaami.playmi.data.model.video.Video
import io.reactivex.Single

class HomeRepository(private val api: HomeApi) {
    fun followChannel(id: Int) = api.followChannel(id)

    fun hideChannel(id: Int) = api.hideChannel(id)

    fun unfollowChannel(id: Int) = api.unfollowChannel(id)

    fun watchLater(videoId:Int) = api.addToLater(videoId)
}