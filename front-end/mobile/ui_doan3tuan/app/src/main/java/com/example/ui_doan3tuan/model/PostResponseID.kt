package com.example.ui_doan3tuan.model

import kotlinx.serialization.Serializable

@Serializable
data class PostResponseID(
    val message: String,
    val user: UserModel,
    val friend_count: Int,
    val post_count:Int,
    val post:List<PostModel>
)
