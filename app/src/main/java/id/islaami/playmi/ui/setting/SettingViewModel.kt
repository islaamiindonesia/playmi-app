package id.islaami.playmi.ui.setting

import androidx.lifecycle.MutableLiveData
import id.islaami.playmi.data.model.setting.Policy
import id.islaami.playmi.data.repository.SettingRepository
import id.islaami.playmi.data.repository.UserRepository
import id.islaami.playmi.ui.base.BaseViewModel
import id.islaami.playmi.util.*

class SettingViewModel(
    private val repository: SettingRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    fun getProfile() = userRepository.profile

    var selectedLocale: String
        get() = userRepository.selectedLocale
        set(value) {
            userRepository.selectedLocale = value
        }

    // Account Name
    lateinit var profileNameResultLd: MutableLiveData<Resource<String>>

    fun getProfileName() {
        disposable.add(userRepository.getProfileName().execute()
            .doOnSubscribe { profileNameResultLd.setLoading() }
            .subscribe(
                { result -> profileNameResultLd.setSuccess(result) },
                { throwable -> profileNameResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    // Logout
    lateinit var logoutResultLd: MutableLiveData<Resource<Any>>

    fun logout() {
        disposable.add(repository.logout().execute()
            .doOnSubscribe { logoutResultLd.setLoading() }
            .subscribe(
                { result -> logoutResultLd.setSuccess(result.data) },
                { throwable -> logoutResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun afterLogout() {
        userRepository.clearCache()
    }

    fun initSettingActivity() {
        logoutResultLd = MutableLiveData()
        profileNameResultLd = MutableLiveData()

        if (getProfile() == null) getProfileName()
    }

    // Report
    lateinit var reportResultLd: MutableLiveData<Resource<Any>>

    fun addReport(desc: String, imageUrl: String) {
        disposable.add(repository.addReport(desc, imageUrl).execute()
            .doOnSubscribe { reportResultLd.setLoading() }
            .subscribe(
                { result -> reportResultLd.setSuccess(result) },
                { throwable -> reportResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    var report = ""
    var imageUrl = ""

    lateinit var reportValid: MutableLiveData<Boolean>

    fun updateReport() {
        reportValid.value = report.isNotEmpty() && imageUrl.isNotEmpty()
    }

    fun initReportActivity() {
        reportValid = MutableLiveData()
        reportResultLd = MutableLiveData()
    }

    // Recommendation
    lateinit var recommendationResultLd: MutableLiveData<Resource<Any>>

    fun addRecommendation(channelName: String, channelUrl: String) {
        disposable.add(repository.addRecommendation(channelName, channelUrl).execute()
            .doOnSubscribe { recommendationResultLd.setLoading() }
            .subscribe(
                { result -> recommendationResultLd.setSuccess(result) },
                { throwable -> recommendationResultLd.setError(throwable.getErrorMessage()) }
            ))
    }


    var channelName = ""
    var channelUrl = ""

    lateinit var formFilled: MutableLiveData<Boolean>

    fun updateFormFilled() {
        formFilled.value = channelName.isNotEmpty() && channelUrl.isNotEmpty()
    }

    fun initRecommendationActivity() {
        formFilled = MutableLiveData()
        recommendationResultLd = MutableLiveData()
    }

    // Insight
    lateinit var insightResultLd: MutableLiveData<Resource<Any>>

    fun addInsight(detail: String) {
        disposable.add(repository.addInsight(detail).execute()
            .doOnSubscribe { insightResultLd.setLoading() }
            .subscribe(
                { result -> insightResultLd.setSuccess(result) },
                { throwable -> insightResultLd.setError(throwable.getErrorMessage()) }
            ))
    }


    fun initInsightActivity() {
        insightResultLd = MutableLiveData()
    }

    // APP POLICY
    lateinit var policyResultLd: MutableLiveData<Resource<Policy>>

    fun aboutApp() {
        disposable.add(repository.aboutApp().execute()
            .doOnSubscribe { policyResultLd.setLoading() }
            .subscribe(
                { result -> policyResultLd.setSuccess(result) },
                { throwable -> policyResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun cooperation() {
        disposable.add(repository.cooperation().execute()
            .doOnSubscribe { policyResultLd.setLoading() }
            .subscribe(
                { result -> policyResultLd.setSuccess(result) },
                { throwable -> policyResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun userTNC() {
        disposable.add(repository.userTNC().execute()
            .doOnSubscribe { policyResultLd.setLoading() }
            .subscribe(
                { result -> policyResultLd.setSuccess(result) },
                { throwable -> policyResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun privacyPolicy() {
        disposable.add(repository.privacyPolicy().execute()
            .doOnSubscribe { policyResultLd.setLoading() }
            .subscribe(
                { result -> policyResultLd.setSuccess(result) },
                { throwable -> policyResultLd.setError(throwable.getErrorMessage()) }
            ))
    }

    fun initPolicyActivity() {
        policyResultLd = MutableLiveData()
    }
}