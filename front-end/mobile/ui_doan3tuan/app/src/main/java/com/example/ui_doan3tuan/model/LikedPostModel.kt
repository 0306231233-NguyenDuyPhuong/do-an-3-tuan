package com.example.ui_doan3tuan.model

import kotlinx.serialization.Serializable
@Serializable
data class LikedPostModel(
    val data: List<dataModel2>
)
@Serializable
data class dataModel2(
    val id: Int,
    val created_at: String?=null,
    val post_id: Int,
    val user_id: Int,
    val User: UserModel?=null,
    val Post: PostModel
)
