package com.example.playmi.data.model.channel

import com.squareup.moshi.Json

data class FollowStatus(
    @field:Json(name = "isFollow") var isFollow: Boolean?
)