package id.islaami.playmi2021.ui

import id.islaami.playmi2021.data.repository.UserRepository
import id.islaami.playmi2021.ui.base.BaseViewModel
import id.islaami.playmi2021.util.execute

class MainViewModel(
    private val userRepository: UserRepository
) : BaseViewModel() {
    fun notifyOnline() {
        disposable.add(userRepository.notifyOnline().execute().doOnSubscribe {

        }.subscribe({

        }, {

        }))
    }
}