package id.islaami.playmi.data.repository

import id.islaami.playmi.data.api.CategoryApi

class CategoryRepository(private val api: CategoryApi) {

    fun getAllCategory() = api.getAllCategory().map { it.data }
}