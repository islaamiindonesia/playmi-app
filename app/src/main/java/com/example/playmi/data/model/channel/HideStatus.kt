package com.example.playmi.data.model.channel

import com.squareup.moshi.Json

data class HideStatus(
    @field:Json(name = "isHidden") var isHidden: Boolean?
)