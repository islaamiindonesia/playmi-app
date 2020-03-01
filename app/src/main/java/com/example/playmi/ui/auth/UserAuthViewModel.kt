package com.example.playmi.ui.auth

import androidx.lifecycle.MutableLiveData
import com.example.playmi.data.model.ApiMessageResult
import com.example.playmi.data.model.profile.Profile
import com.example.playmi.data.repository.UserRepository
import com.example.playmi.ui.base.BaseViewModel
import com.example.playmi.util.*

class UserAuthViewModel(private val repository: UserRepository) : BaseViewModel() {

    fun afterLogout() {
        repository.clearCache()
    }

    // Login
    lateinit var loginResultLd: MutableLiveData<Resource<Profile>>

    fun initLoginActivity() {
        loginResultLd = MutableLiveData()
    }

    fun login(email: String, password: String) {
        disposable.add(repository.login(email, password)
            .execute()
            .doOnSubscribe { loginResultLd.setLoading() }
            .subscribe(
                { result -> loginResultLd.setSuccess(result) },
                { throwable -> loginResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    // Forgot Password
    lateinit var forgotPasswordResultLd: MutableLiveData<Resource<ApiMessageResult>>

    fun initForgotPasswordActivity() {
        forgotPasswordResultLd = MutableLiveData()
    }

    fun forgotPassword(email: String) {
        disposable.add(repository.forgotPassword(email)
            .execute()
            .doOnSubscribe { forgotPasswordResultLd.setLoading() }
            .subscribe(
                { forgotPasswordResult -> forgotPasswordResultLd.setSuccess(forgotPasswordResult) },
                { throwable -> forgotPasswordResultLd.setError("") }
            )
        )
    }

    // Reset Password
    lateinit var resetPasswordResultLd: MutableLiveData<Resource<ApiMessageResult>>

    fun initResetPasswordActivity() {
        resetPasswordResultLd = MutableLiveData()
    }

    fun resetPassword(token: String, password: String) {
        disposable.add(repository.resetPassword(token, password)
            .execute()
            .doOnSubscribe { resetPasswordResultLd.setLoading() }
            .subscribe(
                { resetPasswordResult -> resetPasswordResultLd.setSuccess(resetPasswordResult) },
                { throwable -> resetPasswordResultLd.setError("") }
            )
        )
    }

    // Register
    lateinit var registerResultLd: MutableLiveData<Resource<Profile>>

    fun initRegisterAcitivity() {
        registerResultLd = MutableLiveData()
    }

    fun register(
        fullname: String,
        email: String,
        password: String,
        birthdate: String,
        gender: String,
        notifToken: String
    ) {
        disposable.add(repository.register(fullname, email, password, birthdate, gender, notifToken)
            .execute()
            .doOnSubscribe { registerResultLd.setLoading() }
            .subscribe(
                { registerResult -> registerResultLd.setSuccess(registerResult.data) },
                { throwable -> registerResultLd.setError("") }
            )
        )
    }

    // Verification
    lateinit var verificationResultLd: MutableLiveData<Resource<Any>>

    fun initVerificationActivity() {
        verificationResultLd = MutableLiveData()
    }

    fun verifyUser(email: String, code: String) {
        disposable.add(repository.verify(email, code)
            .execute()
            .doOnSubscribe { verificationResultLd.setLoading() }
            .subscribe(
                { result -> verificationResultLd.setSuccess(result.data) },
                { throwable -> verificationResultLd.setError("") }
            )
        )
    }
}