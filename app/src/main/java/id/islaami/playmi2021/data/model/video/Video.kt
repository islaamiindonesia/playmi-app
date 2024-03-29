package id.islaami.playmi2021.data.model.video

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import id.islaami.playmi2021.data.model.category.Category
import id.islaami.playmi2021.data.model.category.Label
import id.islaami.playmi2021.data.model.category.Subcategory
import id.islaami.playmi2021.data.model.channel.Channel

data class Video(
    @field:Json(name = "id") var ID: Int? = null,
    @field:Json(name = "title") var title: String? = null,
    @field:Json(name = "video_id") var videoID: String? = null,
    @field:Json(name = "url") var url: String? = null,
    @field:Json(name = "thumbnail") var thumbnail: String? = null,
    @field:Json(name = "description") var description: String? = null,
    @field:Json(name = "channel") var channel: Channel? = null,
    @field:Json(name = "category") var category: Category? = null,
    @field:Json(name = "subcategory") var subcategory: Subcategory? = null,
    @field:Json(name = "labels") var labels: List<Label>? = null,
    @field:Json(name = "views") var views: Int? = null,
    @field:Json(name = "published_at") var publishedAt: String? = null,
    @field:Json(name = "is_published") var isPublished: Boolean? = null,
    @field:Json(name = "is_published_now") var isPublishedNow: Boolean? = null,
    @field:Json(name = "is_upload_shown") var isUploadShown: Boolean? = null,
    @field:Json(name = "is_saved_later") var isSavedLater: Boolean? = null,
    @field:Json(name = "series_id") var seriesId: Int? = null,
    @field:Json(name = "series_name") var seriesName: String? = null,
    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Channel::class.java.classLoader),
        parcel.readParcelable(Category::class.java.classLoader),
        parcel.readParcelable(Subcategory::class.java.classLoader),
        parcel.createTypedArrayList(Label),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(ID)
        parcel.writeString(title)
        parcel.writeString(videoID)
        parcel.writeString(url)
        parcel.writeString(thumbnail)
        parcel.writeString(description)
        parcel.writeParcelable(channel, flags)
        parcel.writeParcelable(category, flags)
        parcel.writeParcelable(subcategory, flags)
        parcel.writeTypedList(labels)
        parcel.writeValue(views)
        parcel.writeString(publishedAt)
        parcel.writeValue(isPublished)
        parcel.writeValue(isPublishedNow)
        parcel.writeValue(isUploadShown)
        parcel.writeValue(isSavedLater)
        parcel.writeValue(seriesId)
        parcel.writeString(seriesName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Video> {
        override fun createFromParcel(parcel: Parcel): Video {
            return Video(parcel)
        }

        override fun newArray(size: Int): Array<Video?> {
            return arrayOfNulls(size)
        }
    }
}