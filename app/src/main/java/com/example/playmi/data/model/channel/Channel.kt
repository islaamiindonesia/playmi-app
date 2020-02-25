package com.example.playmi.data.model.channel

import com.squareup.moshi.Json

data class Channel(
    @field:Json(name = "id") var ID: Int?,
    @field:Json(name = "name") var name: String?,
    @field:Json(name = "channel_thumbnail") var thumbnail: String?,
    @field:Json(name = "channel_bio") var bio: String?,
    @field:Json(name = "followers") var followers: Int?,
    @field:Json(name = "is_suspended") var isSuspended: Int?
)