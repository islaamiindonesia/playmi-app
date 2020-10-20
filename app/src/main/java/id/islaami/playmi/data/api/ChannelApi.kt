package id.islaami.playmi.data.api

import id.islaami.playmi.data.model.ApiResponse
import id.islaami.playmi.data.model.channel.Channel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ChannelApi {
    @GET("channels/follow")
    fun getChannelFollow(): Single<ApiResponse<List<Channel>>>

    @GET("channels/follow")
    fun getChannelFollow(@Query("query") name: String): Single<ApiResponse<List<Channel>>>

    @GET("channels/blacklist")
    fun getChannelHidden(): Single<ApiResponse<List<Channel>>>

    @GET("channels/blacklist")
    fun getChannelHidden(@Query("query") name: String): Single<ApiResponse<List<Channel>>>

    @GET("channels/{id}")
    fun getChannelDetail(@Path("id") id: Int): Single<ApiResponse<Channel>>

    @PUT("channels/{id}/hide")
    fun hideChannel(@Path("id") id: Int): Single<ApiResponse<Any>>

    @PUT("channels/{id}/show")
    fun showChannel(@Path("id") id: Int): Single<ApiResponse<Any>>

    @PUT("channels/{id}/follow")
    fun followChannel(@Path("id") id: Int): Single<ApiResponse<Any>>

    @PUT("channels/{id}/unfollow")
    fun unfollowChannel(@Path("id") id: Int): Single<ApiResponse<Any>>
}