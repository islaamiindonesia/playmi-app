package id.islaami.playmi2021.data.model

import com.squareup.moshi.Json

data class ApiMessageResult(
    @field:Json(name = "status") val errorStatus: String?,
    @field:Json(name = "message") val message: String?
)