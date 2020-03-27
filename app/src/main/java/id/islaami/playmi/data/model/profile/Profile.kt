package id.islaami.playmi.data.model.profile

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class Profile(
    @field:Json(name = "id") var ID: String? = null,
    @field:Json(name = "fullname") var fullname: String? = null,
    @field:Json(name = "email") var email: String? = null,
    @field:Json(name = "gender") var gender: String? = null,
    @field:Json(name = "birthdate") var birthdate: String? = null,
    @field:Json(name = "email_verified_at") var verifiedAt: String? = null,
    @field:Json(name = "suspended_at") var suspendedAt: String? = null,
    @field:Json(name = "verification_number") var verificationNumber: String? = null,
    @field:Json(name = "reset_token") var resetToken: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ID)
        parcel.writeString(fullname)
        parcel.writeString(email)
        parcel.writeString(gender)
        parcel.writeString(birthdate)
        parcel.writeString(verifiedAt)
        parcel.writeString(suspendedAt)
        parcel.writeString(verificationNumber)
        parcel.writeString(resetToken)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Profile> {
        override fun createFromParcel(parcel: Parcel): Profile {
            return Profile(parcel)
        }

        override fun newArray(size: Int): Array<Profile?> {
            return arrayOfNulls(size)
        }
    }
}