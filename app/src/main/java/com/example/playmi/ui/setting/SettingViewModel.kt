package com.example.playmi.ui.setting

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.playmi.data.model.ApiMessageResult
import com.example.playmi.data.model.profile.Profile
import com.example.playmi.data.repository.UserRepository
import com.example.playmi.ui.base.BaseViewModel
import com.example.playmi.util.*

class SettingViewModel(private val repository: UserRepository) : BaseViewModel() {

    // Logout
    lateinit var logoutResultLd: MutableLiveData<Resource<Any>>

    fun logout() {
        disposable.add(repository.logout().execute()
            .doOnSubscribe { logoutResultLd.setLoading() }
            .subscribe(
                { logoutResultLd.setSuccess(it) },
                { logoutResultLd.setError(it.getErrorMessage()) }
            ))
    }

    fun afterLogout() {
        repository.clearCache()
    }

    fun initSettingActivity() {
        logoutResultLd = MutableLiveData()
    }
}