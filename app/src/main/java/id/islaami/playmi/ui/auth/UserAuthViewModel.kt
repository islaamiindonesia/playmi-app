package id.islaami.playmi.ui.auth

import androidx.lifecycle.MutableLiveData
import id.islaami.playmi.data.model.profile.LoginResult
import id.islaami.playmi.data.model.profile.Profile
import id.islaami.playmi.data.repository.UserRepository
import id.islaami.playmi.ui.base.BaseViewModel
import id.islaami.playmi.util.*

class UserAuthViewModel(private val repository: UserRepository) : BaseViewModel() {

    fun afterLogout() {
        repository.clearCache()
    }

    // Login
    lateinit var loginResultLd: MutableLiveData<Resource<LoginResult>>
    lateinit var loginByGoogleResultLd: MutableLiveData<Resource<LoginResult>>

    fun initLoginActivity() {
        loginResultLd = MutableLiveData()
        resendCodeResultLd = MutableLiveData()
        loginByGoogleResultLd = MutableLiveData()
    }

    fun initCompletePasswordActvitiy() {
        loginByGoogleResultLd = MutableLiveData()
    }

    fun initCompleteProfileActvitiy() {
        loginResultLd = MutableLiveData()
    }

    fun login(email: String, fcm: String) {
        disposable.add(repository.login(email, fcm)
            .execute()
            .doOnSubscribe { loginResultLd.setLoading() }
            .subscribe(
                { result -> loginResultLd.setSuccess(result) },
                { throwable -> loginResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    // Register
    lateinit var registerResultLd: MutableLiveData<Resource<Profile>>

    fun initRegisterAcitivity() {
        registerResultLd = MutableLiveData()
        loginResultLd = MutableLiveData()
    }

    fun register(
        fullname: String,
        email: String,
        birthdate: String,
        gender: String,
        notifToken: String
    ) {
        disposable.add(repository.register(fullname, email, birthdate, gender, notifToken)
            .execute()
            .doOnSubscribe { registerResultLd.setLoading() }
            .subscribe(
                { registerResult -> registerResultLd.setSuccess(registerResult.data) },
                { throwable -> registerResultLd.setError(throwable.getErrorMessage()) }
            )
        )
    }

    fun registerFromGoogle(
        fullname: String,
        email: String,
        birthdate: String,
        gender: String,
        notifToken: String
    ) {
        disposable.add(repository.registerFromGoogle(fullname, email, birthdate, gender, notifToken)
            .execute()
            .doOnSubscribe { loginResultLd.setLoading() }
            .subscribe(
                { result -> loginResultLd.setSuccess(result) },
                { throwable -> loginResultLd.setError(throwable.getErrorMessage()) }
            )
        )
    }

    // Verification
    lateinit var verificationResultLd: MutableLiveData<Resource<LoginResult>>
    lateinit var resendCodeResultLd: MutableLiveData<Resource<Any>>

    fun initVerificationActivity() {
        verificationResultLd = MutableLiveData()
        resendCodeResultLd = MutableLiveData()
    }

    fun verifyUser(email: String, code: String) {
        disposable.add(repository.verify(email, code)
            .execute()
            .doOnSubscribe { verificationResultLd.setLoading() }
            .subscribe(
                { result -> verificationResultLd.setSuccess(result) },
                { throwable -> verificationResultLd.setError(throwable.getErrorMessage()) }
            )
        )
    }

    fun resendCode(email: String, token: String) {
        disposable.add(repository.resendEmail(email, token)
            .execute()
            .doOnSubscribe { resendCodeResultLd.setLoading() }
            .subscribe(
                { result -> resendCodeResultLd.setSuccess(result) },
                { throwable -> resendCodeResultLd.setError(throwable.getErrorMessage()) }
            )
        )
    }
}