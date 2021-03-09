package id.islaami.playmi2021.data.api

import id.islaami.playmi2021.data.model.ApiResponse
import id.islaami.playmi2021.data.model.category.Category
import io.reactivex.Single
import retrofit2.http.GET

interface CategoryApi {
    @GET("categories")
    fun getAllCategory(): Single<ApiResponse<List<Category>>>
}