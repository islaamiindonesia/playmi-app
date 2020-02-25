package com.example.playmi.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Kemal Amru Ramadhan on 12/04/2019.
 */
abstract class BaseViewModel : ViewModel() {

    val disposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}