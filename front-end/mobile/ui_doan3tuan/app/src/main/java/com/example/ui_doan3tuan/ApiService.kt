package com.example.ui_doan3tuan

import com.example.ui_doan3tuan.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // Auth
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>
    // 1. Lấy danh sách lời mời kết bạn
    @GET("friends/requests")
    fun getFriendRequests(
        @Header("Authorization") token: String
    ): Call<FriendRequestResponse>

    // 2. Chấp nhận lời mời
    @PATCH("friends/requests/accept")
    fun acceptRequest(
        @Header("Authorization") token: String,
        @Body request: AcceptRequest
    ): Call<Void>

    // 3. Từ chối lời mời
    @PATCH("friends/requests/reject")
    fun rejectRequest(
        @Header("Authorization") token: String,
        @Body request: RejectRequest
    ): Call<Void>

    // 4. Lấy danh sách bạn bè
    @GET("friends")
    fun getFriendList(
        @Header("Authorization") token: String
    ): Call<FriendListResponse>

    @POST("friends/requests/send")
    fun sendFriendRequest(
        @Header("Authorization") token: String,
        @Body request: SendRequest
    ): Call<ApiMessage>

    @PATCH("friends/unfriend")
    fun unfriend(
        @Header("Authorization") token: String,
        @Body request: UnfriendRequest
    ): Call<ApiMessage>
}

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8989/api/"

    val apiService: ApiService by lazy {
        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
