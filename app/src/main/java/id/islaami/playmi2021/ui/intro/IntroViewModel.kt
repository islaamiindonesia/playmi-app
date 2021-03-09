package id.islaami.playmi2021.ui.intro

import id.islaami.playmi2021.data.repository.UserRepository
import id.islaami.playmi2021.ui.base.BaseViewModel

class IntroViewModel(val repository: UserRepository) : BaseViewModel() {
    var hasSeenIntro: Boolean
        get() = repository.hasSeenIntro
        set(value) {
            repository.hasSeenIntro = value
        }

    fun isLoggedIn() = repository.isLoggedIn()
}