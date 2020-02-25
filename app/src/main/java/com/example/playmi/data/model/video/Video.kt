package com.example.playmi.data.model.video

import com.squareup.moshi.Json

data class Video(
    @field:Json(name = "id") var ID: Int?,
    @field:Json(name = "title") var title: String?,
    @field:Json(name = "video_url") var url: String?,
    @field:Json(name = "video_thumbnail") var thumbnail: String?,
    @field:Json(name = "description") var description: String?,
    @field:Json(name = "channel_id") var channelID: Int?,
    @field:Json(name = "channel") var channel: String?,
    @field:Json(name = "channel_thumbnail") var channelThumbnail: String?,
    @field:Json(name = "category_id") var categoryID: Int?,
    @field:Json(name = "category") var category: String?,
    @field:Json(name = "subcategory_id") var subcategoryID: Int?,
    @field:Json(name = "subcategory") var subcategory: String?,
    @field:Json(name = "views") var views: Int?,
    @field:Json(name = "followers") var followers: Int?,
    @field:Json(name = "follow_status") var followStatus: Boolean?,
    @field:Json(name = "published_at") var publishedAt: String?
)