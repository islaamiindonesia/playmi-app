package id.islaami.playmi2021.data.model.category

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class Subcategory(
    @field:Json(name = "id") var ID: Int?,
    @field:Json(name = "name") var name: String?,
    @field:Json(name = "number") var orderNumber: Int?,
    @field:Json(name = "created_at") var createdAt: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(ID)
        parcel.writeString(name)
        parcel.writeValue(orderNumber)
        parcel.writeString(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Subcategory> {
        override fun createFromParcel(parcel: Parcel): Subcategory {
            return Subcategory(parcel)
        }

        override fun newArray(size: Int): Array<Subcategory?> {
            return arrayOfNulls(size)
        }
    }
}