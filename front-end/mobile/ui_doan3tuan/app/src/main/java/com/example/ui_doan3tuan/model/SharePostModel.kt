package com.example.ui_doan3tuan.model

import kotlinx.serialization.Serializable
@Serializable
data class SharePostModel (
    val data: List<dataModel>
)
@Serializable
data class dataModel(
    val id: Int,
    val created_at: String?=null,
    val post_id: Int,
    val user_id: Int,
    val User: UserModel?=null,
    val Post: PostModel
)
