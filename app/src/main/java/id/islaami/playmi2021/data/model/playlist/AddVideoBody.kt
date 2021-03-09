package id.islaami.playmi2021.data.model.playlist

import com.squareup.moshi.Json

data class AddVideoBody(
    @field:Json(name = "video_id") var videoID: Int? = null,
    @field:Json(name = "playlists") var playlistIDs: List<Int>? = null
)