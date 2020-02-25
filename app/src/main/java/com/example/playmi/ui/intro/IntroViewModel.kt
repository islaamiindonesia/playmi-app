package com.example.playmi.ui.intro

import com.example.playmi.data.repository.UserRepository
import com.example.playmi.ui.base.BaseViewModel

class IntroViewModel(private val repository: UserRepository) : BaseViewModel() {

    var hasSeenIntro: Boolean
        get() = repository.hasSeenIntro
        set(value) {
            repository.hasSeenIntro = value
        }

    fun clearCache() {
        repository.clearCache()
    }

    fun isLoggedIn() = repository.isLoggedIn()
}