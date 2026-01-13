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

    // 1. Gửi lời mời kết bạn
    @POST("friends/requests")
    fun sendFriendRequest(
        @Header("Authorization") token: String,
        @Body request: SendRequest
    ): Call<ApiMessage>

    // 2. Hủy kết bạn
    @POST("friends/unfriend")
    fun unfriend(
        @Header("Authorization") token: String,
        @Body request: UnfriendRequest
    ): Call<ApiMessage>

    // 3. Lấy danh sách bạn bè
    @GET("friends")
    fun getFriendList(
        @Header("Authorization") token: String
    ): Call<FriendListResponse>

    // 4. Lấy danh sách lời mời
    @GET("friends/requests")
    fun getFriendRequests(
        @Header("Authorization") token: String
    ): Call<FriendRequestResponse>

    // 5. Chấp nhận lời mời
    @PATCH("friends/requests/accept")
    fun acceptRequest(
        @Header("Authorization") token: String,
        @Body request: AcceptRequest
    ): Call<Void>

    // 6. Từ chối lời mời
    @PATCH("friends/requests/reject")
    fun rejectRequest(
        @Header("Authorization") token: String,
        @Body request: RejectRequest
    ): Call<Void>

    // Search users
    @GET("users/search")
    fun searchUsers(
        @Header("Authorization") token: String,
        @Query("keyword") keyword: String
    ): Call<UserSearchResponse>
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