package id.islaami.playmi2021.data.repository

import id.islaami.playmi2021.data.api.ChannelApi

class ChannelRepository(private val api: ChannelApi) {

    fun getChannelFollow(query: String? = null) =
        if (query.isNullOrEmpty()) {
            api.getChannelFollow().map { it.data }
        } else {
            api.getChannelFollow(query).map { it.data }
        }

    fun getChannelHidden(query: String? = null) =
        if (query.isNullOrEmpty()) {
            api.getChannelHidden().map { it.data }
        } else {
            api.getChannelHidden(query).map { it.data }
        }

    fun getDetailChannel(channelID: Int) = api.getChannelDetail(channelID).map { it.data }

    fun hideChannel(id: Int) = api.hideChannel(id)

    fun showChannel(id: Int) = api.showChannel(id)

    fun followChannel(id: Int) = api.followChannel(id)

    fun unfollowChannel(id: Int) = api.unfollowChannel(id)
}