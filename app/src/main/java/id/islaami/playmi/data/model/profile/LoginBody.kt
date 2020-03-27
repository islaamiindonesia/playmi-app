package id.islaami.playmi.data.model.profile

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class LoginBody(
    @field:Json(name = "email") var email: String? = null,
    @field:Json(name = "fcm_token") var token: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(token)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoginBody> {
        override fun createFromParcel(parcel: Parcel): LoginBody {
            return LoginBody(parcel)
        }

        override fun newArray(size: Int): Array<LoginBody?> {
            return arrayOfNulls(size)
        }
    }
}