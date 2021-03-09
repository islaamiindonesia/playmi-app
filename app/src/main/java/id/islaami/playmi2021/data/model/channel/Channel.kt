package id.islaami.playmi2021.data.model.channel

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class Channel(
    @field:Json(name = "id") var ID: Int? = null,
    @field:Json(name = "name") var name: String? = null,
    @field:Json(name = "thumbnail") var thumbnail: String? = null,
    @field:Json(name = "description") var bio: String? = null,
    @field:Json(name = "created_at") var createdAt: String? = null,
    @field:Json(name = "updated_at") var updatedAt: String? = null,
    @field:Json(name = "suspended_at") var suspendedAt: String? = null,
    @field:Json(name = "followers") var followers: Int? = null,
    @field:Json(name = "is_followed") var isFollowed: Boolean? = null,
    @field:Json(name = "is_blacklisted") var isBlacklisted: Boolean? = null,
    @field:Json(name = "videos") var videos: Int? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(ID)
        parcel.writeString(name)
        parcel.writeString(thumbnail)
        parcel.writeString(bio)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeString(suspendedAt)
        parcel.writeValue(followers)
        parcel.writeValue(isFollowed)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Channel> {
        override fun createFromParcel(parcel: Parcel): Channel {
            return Channel(parcel)
        }

        override fun newArray(size: Int): Array<Channel?> {
            return arrayOfNulls(size)
        }
    }
}