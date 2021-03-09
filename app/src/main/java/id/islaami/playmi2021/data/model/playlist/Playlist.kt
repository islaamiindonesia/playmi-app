package id.islaami.playmi2021.data.model.playlist

import android.os.Parcel
import android.os.Parcelable
import id.islaami.playmi2021.data.model.video.Video
import com.squareup.moshi.Json

data class Playlist(
    @field:Json(name = "id") var ID: Int? = null,
    @field:Json(name = "name") var name: String? = null,
    @field:Json(name = "video_count") var videoCount: Int? = null,
    @field:Json(name = "videos") var videos: List<Video>? = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.createTypedArrayList(Video)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(ID)
        parcel.writeString(name)
        parcel.writeValue(videoCount)
        parcel.writeTypedList(videos)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Playlist> {
        override fun createFromParcel(parcel: Parcel): Playlist {
            return Playlist(parcel)
        }

        override fun newArray(size: Int): Array<Playlist?> {
            return arrayOfNulls(size)
        }
    }
}