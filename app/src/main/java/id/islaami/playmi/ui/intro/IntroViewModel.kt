package id.islaami.playmi.ui.intro

import id.islaami.playmi.data.repository.UserRepository
import id.islaami.playmi.ui.base.BaseViewModel

class IntroViewModel(private val repository: UserRepository) : BaseViewModel() {
    var darkMode: Int
        get() = repository.darkMode
        set(value) {
            repository.darkMode = value
        }

    fun isLoggedIn() = repository.isLoggedIn()
}