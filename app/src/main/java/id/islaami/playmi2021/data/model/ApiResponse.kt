package id.islaami.playmi2021.data.model

import com.squareup.moshi.Json

data class ApiResponse<T>(
    @field:Json(name = "data") val data: T? = null,
    @field:Json(name = "status") val status: Boolean? = false,
    @field:Json(name = "message") val message: String?
)