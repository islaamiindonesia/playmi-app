package com.example.playmi.data.model.video

import com.squareup.moshi.Json

data class VideoData(
    @field:Json(name = "videos") var videos: List<Video> = emptyList()
)