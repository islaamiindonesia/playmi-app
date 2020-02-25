package com.example.playmi.util

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Kemal Amru Ramadhan on 22/04/2019.
 */

fun <T> Single<T>.execute(): Single<T> =
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())