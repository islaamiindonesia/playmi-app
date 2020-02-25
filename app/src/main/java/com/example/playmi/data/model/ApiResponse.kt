package com.example.playmi.data.model

import com.squareup.moshi.Json

data class ApiResponse<T>(
    @field:Json(name = "data") val data: T?,
    @field:Json(name = "status") val status: Boolean?,
    @field:Json(name = "message") val message: String?
)