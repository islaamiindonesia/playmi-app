package com.example.playmi.data.repository

import com.example.playmi.data.api.CategoryApi

class CategoryRepository(private val api: CategoryApi) {

    fun getAllCategory() = api.getAllCategory().map { it.data }
}