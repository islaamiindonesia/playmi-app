package id.islaami.playmi2021.util

import androidx.lifecycle.MutableLiveData
import id.islaami.playmi2021.data.model.ErrorResponse

/** FYI about LiveData and MutableLiveData
 * MutableLiveData is a LiveData object whose value can be changed. MutableLiveData is a generic class, so you need to specify the type of data that it holds.
 * LiveData holds data; it is a wrapper that can be used with any data. LiveData is lifecycle-aware, meaning that it only updates observers that are in an active lifecycle state such as STARTED or RESUMED .
 */
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
