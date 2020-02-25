package com.example.playmi.data.repository

import com.example.playmi.data.api.VideoApi
import com.example.playmi.data.cache.UserCache
import com.example.playmi.data.model.notification.NotificationSetting

class VideoRepository(
    private val userCache: UserCache,
    private val api: VideoApi
) {
    var notificationSetting: NotificationSetting?
        get() = userCache.notificationSetting
        set(value) {
            userCache.notificationSetting = value
        }

    fun getAllVideo(page: Int, query: String? = null, category: String? = null) =
        api.getAllVideo(page, query = query, category = category).map { it.data }

    fun getAllVideoByFollowing(page: Int) =
        api.getAllVideoByFollowing(page).map { it.data }

    fun getVideo(id: Int) = api.getVideo(id).map { it.data }

    fun watchLater(videoId: Int) = api.addToLater(videoId)
}