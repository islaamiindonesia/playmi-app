package id.islaami.playmi2021.data.model.profile

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class LoginResult(
    @field:Json(name = "user") var user: Profile? = null,
    @field:Json(name = "auth_token") var token: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Profile::class.java.classLoader),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(user, flags)
        parcel.writeString(token)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoginResult> {
        override fun createFromParcel(parcel: Parcel): LoginResult {
            return LoginResult(parcel)
        }

        override fun newArray(size: Int): Array<LoginResult?> {
            return arrayOfNulls(size)
        }
    }
}