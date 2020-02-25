package com.example.playmi.util

import androidx.lifecycle.MutableLiveData

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
