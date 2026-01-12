package com.example.ui_doan3tuan.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostResponseIDModel(
    val message: String,
    val user: UserModel,
    val friend_count: Int,
    val post_count:Int,
    val post:List<PostModel>
)


