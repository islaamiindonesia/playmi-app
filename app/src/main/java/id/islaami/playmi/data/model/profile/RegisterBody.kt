package id.islaami.playmi.data.model.profile

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class RegisterBody(
    @field:Json(name = "email") var email: String? = null,
    @field:Json(name = "fullname") var fullname: String? = null,
    @field:Json(name = "birthdate") var birthdate: String? = null,
    @field:Json(name = "gender") var gender: String? = null,
    @field:Json(name = "fcm_token") var token: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(fullname)
        parcel.writeString(birthdate)
        parcel.writeString(gender)
        parcel.writeString(token)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RegisterBody> {
        override fun createFromParcel(parcel: Parcel): RegisterBody {
            return RegisterBody(parcel)
        }

        override fun newArray(size: Int): Array<RegisterBody?> {
            return arrayOfNulls(size)
        }
    }
}