package com.example.playmi.data.model.playlist

import com.squareup.moshi.Json

data class Playlist(
    @field:Json(name = "id") var ID: Int?,
    @field:Json(name = "name") var name: String?,
    @field:Json(name = "video_count") var videoCount: Int?
)