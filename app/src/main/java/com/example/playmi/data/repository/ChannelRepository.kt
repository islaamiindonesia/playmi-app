package com.example.playmi.data.repository

import com.example.playmi.data.api.ChannelApi

class ChannelRepository(private val api: ChannelApi) {

    fun getAllChannel(page: Int, query: String? = null) =
        api.getAllChannel(page, query = query).map { it.data }

    fun getDetailChannel(channelID: Int) = api.getChannelDetail(channelID).map { it.data }

    fun getChannelVideos(channelID: Int) = api.getChannelVideos(channelID).map { it.data }

    fun getFollowingStatus(id: Int) = api.getFollowingStatus(id).map { it.data?.isFollow }

    fun getHideStatus(id: Int) = api.getHideStatus(id).map { it.data?.isHidden }

    fun hideChannel(id: Int) = api.hideChannel(id)

    fun followChannel(id: Int) = api.followChannel(id)

    fun unfollowChannel(id: Int) = api.unfollowChannel(id)
}