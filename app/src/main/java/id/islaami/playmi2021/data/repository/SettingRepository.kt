package id.islaami.playmi2021.data.repository

import id.islaami.playmi2021.data.api.SettingApi
import id.islaami.playmi2021.data.api.UserApi
import id.islaami.playmi2021.data.cache.UserCache

class SettingRepository(
    private val userCache: UserCache,
    private val userApi: UserApi,
    private val api: SettingApi
) {
    fun logout() = userApi.logout()

    fun addReport(desc: String, imageUrl: String) =
        api.addReport(description = desc, imageUrl = imageUrl).map { it.data }

    fun addRecommendation(channelName: String, channelUrl: String) =
        api.addRecommendation(channelName = channelName, channelUrl = channelUrl).map { it.data }

    fun addInsight(detail: String) = api.addInsight(detail = detail).map { it.data }

    fun aboutApp() = api.aboutApp().map { it.data }
    fun cooperation() = api.cooperation().map { it.data }
    fun userTNC() = api.userTNC().map { it.data }
    fun privacyPolicy() = api.privacyPolicy().map { it.data }
}