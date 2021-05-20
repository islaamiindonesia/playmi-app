package id.islaami.playmi2021.ui.setting.profile

import androidx.lifecycle.MutableLiveData
import id.islaami.playmi2021.data.model.profile.Profile
import id.islaami.playmi2021.data.repository.UserRepository
import id.islaami.playmi2021.ui.base.BaseViewModel
import id.islaami.playmi2021.util.*

class ProfileViewModel(
    private val userRepository: UserRepository
) : BaseViewModel() {

    fun getProfile() = userRepository.profile

    val profileResultLd = MutableLiveData<Resource<Profile>>()
    val updateProfileResultLd = MutableLiveData<Resource<Any>>()

    fun getProfileDetail() {
        disposable.add(userRepository.getProfile().execute()
            .doOnSubscribe { profileResultLd.setLoading() }
            .subscribe(
                { result -> profileResultLd.setSuccess(result) },
                { throwable -> profileResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun updateProfile(
        fullname: String,
        email: String,
        birthdate: String,
        gender: String,
    ) {
        disposable.add(userRepository.updateProfile(fullname, email, birthdate, gender).execute()
            .doOnSubscribe { updateProfileResultLd.setLoading() }
            .subscribe({ result ->
                updateProfileResultLd.setSuccess(result)
            }, { throwable ->
                updateProfileResultLd.setError(throwable.getErrorMessage())
            })
        )
    }
}