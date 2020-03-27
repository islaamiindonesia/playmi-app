package id.islaami.playmi.util

import androidx.lifecycle.MutableLiveData
import id.islaami.playmi.data.model.ErrorResponse

fun <T> MutableLiveData<Resource<T>>.setSuccess(data: T? = null) =
    postValue(
        Resource(
            ResourceStatus.SUCCESS,
            data
        )
    )

fun <T> MutableLiveData<Resource<T>>.setLoading(data: T? = null) =
    postValue(
        Resource(
            ResourceStatus.LOADING,
            data
        )
    )

fun <T> MutableLiveData<Resource<T>>.setError(message: String? = null) =
    postValue(
        Resource(
            ResourceStatus.ERROR,
            value?.data,
            message
        )
    )

fun <T> MutableLiveData<Resource<T>>.setError(errorResponse: ErrorResponse? = null) =
    postValue(
        Resource(
            ResourceStatus.ERROR,
            value?.data,
            errorResponse = errorResponse
        )
    )
