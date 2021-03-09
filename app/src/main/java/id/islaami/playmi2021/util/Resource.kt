package id.islaami.playmi2021.util

import id.islaami.playmi2021.data.model.ErrorResponse

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
