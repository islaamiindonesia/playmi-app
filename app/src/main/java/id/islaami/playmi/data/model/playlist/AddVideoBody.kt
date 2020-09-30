package id.islaami.playmi.data.model.playlist

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class AddVideoBody(
    @field:Json(name = "video_id") var videoID: Int? = null,
    @field:Json(name = "playlists") var playlistIDs: List<Int>? = null
)