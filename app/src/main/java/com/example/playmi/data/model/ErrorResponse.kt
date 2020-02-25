package com.example.playmi.data.model

import com.squareup.moshi.Json

data class ErrorResponse(
    @field:Json(name = "status") val status: Boolean? = false,
    @field:Json(name = "message") val message: String?
)