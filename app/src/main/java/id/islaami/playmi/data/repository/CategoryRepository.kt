package id.islaami.playmi.data.repository

import id.islaami.playmi.data.api.CategoryApi
import id.islaami.playmi.data.model.category.Category

class CategoryRepository(private val api: CategoryApi) {

    fun getAllCategory() = api.getAllCategory().map {
        val list = ArrayList<Category>()
        list.add(Category(0, "Semua", 0, ""))
        it.data?.forEach { category -> list.add(category) }

        list
    }
}