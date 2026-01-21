package com.example.ui_doan3tuan.model

import com.google.gson.annotations.SerializedName


data class UserPostResponse(
    val message: String,

    @SerializedName("like_count")
    val likeCount: Int,

    @SerializedName("comment_count")
    val commentCount: Int,

    @SerializedName("post_count")
    val postCount: Int,

    @SerializedName("friend_count")
    val friendCount: Int,

    val page: Int,
    val limit: Int,

    val user: UserModel,

    @SerializedName("post")
    val posts: List<PostModel>?
)
