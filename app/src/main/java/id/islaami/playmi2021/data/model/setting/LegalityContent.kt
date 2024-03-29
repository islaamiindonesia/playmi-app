package id.islaami.playmi2021.data.model.setting

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class LegalityContent(
    @field:Json(name = "name") var name: String? = null,
    @field:Json(name = "content") var content: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(content)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LegalityContent> {
        override fun createFromParcel(parcel: Parcel): LegalityContent {
            return LegalityContent(parcel)
        }

        override fun newArray(size: Int): Array<LegalityContent?> {
            return arrayOfNulls(size)
        }
    }
}