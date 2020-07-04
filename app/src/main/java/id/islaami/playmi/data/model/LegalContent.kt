package id.islaami.playmi.data.model

import com.squareup.moshi.Json

data class LegalContent(
    @field:Json(name = "content") var content: String? = null
)