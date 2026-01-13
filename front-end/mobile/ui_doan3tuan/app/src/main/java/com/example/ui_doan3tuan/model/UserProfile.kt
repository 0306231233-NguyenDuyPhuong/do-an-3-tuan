package com.example.ui_doan3tuan.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile (
    val like_count:Int,
    val comment_count:Int,
    val post_count:Int,
    val friend_count:Int,
    val page:Int,
    val user: UserModel,
    val post: List<PostModel>
)