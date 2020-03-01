package com.example.playmi.data.repository

import com.example.playmi.data.api.ChannelApi
import com.example.playmi.data.api.VideoApi
import com.example.playmi.data.cache.UserCache
import com.example.playmi.data.model.notification.NotificationSetting

class VideoRepository(
    private val userCache: UserCache,
    private val video: VideoApi,
    private val channel: ChannelApi
) {
    var notificationSetting: NotificationSetting?
        get() = userCache.notificationSetting
        set(value) {
            userCache.notificationSetting = value
        }

    fun getAllVideo(page: Int, query: String? = null, category: String? = null) =
        video.getAllVideo(page, query = query, category = category).map { it.data }

    fun getAllVideoByFollowing(page: Int) =
        video.getAllVideoByFollowing(page).map { it.data }

    fun getVideo(id: Int) = video.getVideo(id).map { it.data }

    fun watchLater(videoId: Int) = video.addToLater(videoId)

    fun hideChannel(id: Int) = channel.hideChannel(id)

    fun followChannel(id: Int) = channel.followChannel(id)

    fun unfollowChannel(id: Int) = channel.unfollowChannel(id)
}