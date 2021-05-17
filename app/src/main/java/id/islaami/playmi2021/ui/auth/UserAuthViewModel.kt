package id.islaami.playmi2021.ui.auth

import androidx.lifecycle.MutableLiveData
import id.islaami.playmi2021.data.model.profile.LoginResult
import id.islaami.playmi2021.data.model.profile.Profile
import id.islaami.playmi2021.data.repository.UserRepository
import id.islaami.playmi2021.ui.base.BaseViewModel
import id.islaami.playmi2021.util.*

class UserAuthViewModel(private val repository: UserRepository) : BaseViewModel() {
    /* LOGOUT */
    fun afterLogout() {
        repository.clearCache()
    }

    /* LOGIN */
    lateinit var loginResultLd: MutableLiveData<Resource<LoginResult>>

    fun login(email: String, fcm: String) {
        disposable.add(repository.login(email, fcm)
            .execute()
            .doOnSubscribe { loginResultLd.setLoading() }
            .subscribe(
                { result -> loginResultLd.setSuccess(result) },
                { throwable -> loginResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun loginWithGoogle(email: String, fcm: String) {
        disposable.add(repository.login(email, fcm)
            .execute()
            .doOnSubscribe { loginResultLd.setLoading() }
            .subscribe(
                { result -> loginResultLd.setSuccess(result) },
                { throwable -> loginResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    /* REGISTER */
    lateinit var registerResultLd: MutableLiveData<Resource<Profile>>

    fun register(
        fullname: String,
        email: String,
        birthdate: String,
        gender: String,
        notifToken: String
    ) {
        disposable.add(repository.register(fullname, email, birthdate, gender, notifToken)
            .execute()
            .doOnSubscribe { loginResultLd.setLoading() }
            .subscribe(
                { result -> loginResultLd.setSuccess(result) },
                { throwable -> loginResultLd.setError(throwable.getErrorMessage()) }
            )
        )
    }

    /* VERIFICATION */
    lateinit var resendCodeResultLd: MutableLiveData<Resource<Any>>
    lateinit var verifiedResultLd: MutableLiveData<Resource<Any>>


    fun resendCode(email: String, name: String) {
        disposable.add(repository.resendEmail(email, name)
            .execute()
            .doOnSubscribe { resendCodeResultLd.setLoading() }
            .subscribe(
                { result -> resendCodeResultLd.setSuccess(result) },
                { throwable -> resendCodeResultLd.setError(throwable.getErrorMessage()) }
            )
        )
    }

    fun verify(email: String) {
        disposable.add(
            repository.verify(email).execute().doOnSubscribe { verifiedResultLd.setLoading() }
                .subscribe(
                    { result -> verifiedResultLd.setSuccess(result) },
                    { throwable -> verifiedResultLd.setError(throwable.getErrorMessage()) }
                )
        )
    }

    /* INIT LOGIN */
    fun initLoginActivity() {
        loginResultLd = MutableLiveData()
        resendCodeResultLd = MutableLiveData()
    }

    /* INIT VERIFICATION */
    fun initVerificationActivity() {
        resendCodeResultLd = MutableLiveData()
    }

    /* INIT REGISTER */
    fun initRegisterAcitivity() {
        registerResultLd = MutableLiveData()
        loginResultLd = MutableLiveData()
        verifiedResultLd = MutableLiveData()
    }
}