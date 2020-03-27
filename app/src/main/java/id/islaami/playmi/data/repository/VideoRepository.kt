package id.islaami.playmi.data.repository

import id.islaami.playmi.data.api.ChannelApi
import id.islaami.playmi.data.api.VideoApi
import id.islaami.playmi.data.cache.UserCache
import id.islaami.playmi.data.model.notification.NotificationSetting

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

    fun getAllVideo(page: Int) =
        video.getAllVideo(page).map { it.data }

    fun getAllVideoByChannel(page: Int, id: Int) =
        video.getAllVideoByChannel(page = page, id = id).map { it.data }

    fun getAllVideoByCategory(page: Int, id: Int) =
        video.getAllVideoByCategory(page = page, id = id).map { it.data }

    fun getAllVideoByFollowing(page: Int) =
        video.getAllVideoByFollowing(page).map { it.data }

    fun getVideo(id: Int) = video.getVideo(id).map { it.data }

    fun watchLater(videoId: Int) = video.addWatchLater(videoId)

    fun hideChannel(id: Int) = channel.hideChannel(id)

    fun followChannel(id: Int) = channel.followChannel(id)

    fun unfollowChannel(id: Int) = channel.unfollowChannel(id)
}