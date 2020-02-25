package com.example.playmi.data.model.profile


import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class Profile(
    @field:Json(name = "id") var ID: String?,
    @field:Json(name = "aud_id") var audID: String?,
    @field:Json(name = "fullname") var fullname: String?,
    @field:Json(name = "gender") var gender: String?,
    @field:Json(name = "email") var email: String?,
    @field:Json(name = "birthdate") var birthdate: String?,
    @field:Json(name = "api_token") var authToken: String?,
    @field:Json(name = "notif_token") var notifToken: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
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
        parcel.writeString(audID)
        parcel.writeString(fullname)
        parcel.writeValue(gender)
        parcel.writeString(email)
        parcel.writeString(birthdate)
        parcel.writeString(authToken)
        parcel.writeString(notifToken)
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