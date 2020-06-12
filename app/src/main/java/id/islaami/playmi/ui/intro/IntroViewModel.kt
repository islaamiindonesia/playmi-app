package id.islaami.playmi.ui.intro

import id.islaami.playmi.data.repository.UserRepository
import id.islaami.playmi.ui.base.BaseViewModel

class IntroViewModel(val repository: UserRepository) : BaseViewModel() {
    var hasSeenIntro: Boolean
        get() = repository.hasSeenIntro
        set(value) {
            repository.hasSeenIntro = value
        }

    fun isLoggedIn() = repository.isLoggedIn()
}