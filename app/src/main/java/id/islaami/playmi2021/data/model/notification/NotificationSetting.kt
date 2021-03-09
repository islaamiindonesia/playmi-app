package id.islaami.playmi2021.data.model.notification

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class NotificationSetting(
    @field:Json(name = "channel_id") var channelID: Int?,
    @field:Json(name = "notif_status") var notifStatus: Boolean?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(channelID)
        parcel.writeValue(notifStatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NotificationSetting> {
        override fun createFromParcel(parcel: Parcel): NotificationSetting {
            return NotificationSetting(parcel)
        }

        override fun newArray(size: Int): Array<NotificationSetting?> {
            return arrayOfNulls(size)
        }
    }
}