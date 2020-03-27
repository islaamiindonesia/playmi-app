package id.islaami.playmi.data.model

import com.squareup.moshi.Json

data class ErrorResponse(
    @field:Json(name = "status") val status: Boolean? = false,
    @field:Json(name = "message") val message: String?,
    @field:Json(name = "userMsg") val userMessage: String?
)