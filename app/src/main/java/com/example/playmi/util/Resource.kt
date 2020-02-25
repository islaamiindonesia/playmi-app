package com.example.playmi.util

/**
 * Resource class is Data Wrapper
 */
data class Resource<out T> constructor(
    val status: ResourceStatus,
    val data: T? = null,
    val message: String? = null
)

enum class ResourceStatus {LOADING, SUCCESS, ERROR}
