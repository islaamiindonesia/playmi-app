package id.islaami.playmi2021.data.repository

import id.islaami.playmi2021.data.api.HomeApi

class HomeRepository(private val api: HomeApi) {
    fun followChannel(id: Int) = api.followChannel(id)

    fun hideChannel(id: Int) = api.hideChannel(id)

    fun unfollowChannel(id: Int) = api.unfollowChannel(id)

    fun watchLater(videoId:Int) = api.addToLater(videoId)
}