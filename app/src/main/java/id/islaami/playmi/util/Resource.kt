package id.islaami.playmi.util

import id.islaami.playmi.data.model.ErrorResponse

/**
 * Resource class is Data Wrapper
 */
data class Resource<out T> constructor(
    val status: ResourceStatus,
    val data: T? = null,
    val message: String? = null,
    val errorResponse: ErrorResponse? = null
)

enum class ResourceStatus {LOADING, SUCCESS, ERROR}
