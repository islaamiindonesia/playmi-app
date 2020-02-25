package com.example.playmi.data.api

import com.example.playmi.data.model.ApiResponse
import com.example.playmi.data.model.category.Category
import io.reactivex.Single
import retrofit2.http.GET

interface CategoryApi {
    @GET("categories")
    fun getAllCategory(): Single<ApiResponse<List<Category>>>
}