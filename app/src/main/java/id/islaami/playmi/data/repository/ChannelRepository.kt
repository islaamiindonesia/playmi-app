package id.islaami.playmi.data.repository

import android.util.Log
import id.islaami.playmi.data.api.ChannelApi

class ChannelRepository(private val api: ChannelApi) {

    fun getChannelFollow() = api.getChannelFollow().map { it.data }

    fun getChannelHidden() = api.getChannelHidden().map { it.data }

    fun getDetailChannel(channelID: Int) = api.getChannelDetail(channelID).map { it.data }

    fun hideChannel(id: Int) = api.hideChannel(id)

    fun showChannel(id: Int) = api.showChannel(id)

    fun followChannel(id: Int) = api.followChannel(id)

    fun unfollowChannel(id: Int) = api.unfollowChannel(id)
}