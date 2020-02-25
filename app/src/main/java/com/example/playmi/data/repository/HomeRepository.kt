package com.example.playmi.data.repository

import com.example.playmi.data.api.HomeApi
import com.example.playmi.data.model.video.Video
import io.reactivex.Single

class HomeRepository(private val api: HomeApi) {
    fun getFollowingStatus(id: Int) = api.getFollowingStatus(id).map { it.data?.isFollow }

    fun followChannel(id: Int) = api.followChannel(id)

    fun hideChannel(id: Int) = api.hideChannel(id)

    fun unfollowChannel(id: Int) = api.unfollowChannel(id)

    fun watchLater(videoId:Int) = api.addToLater(videoId)
}