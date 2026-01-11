package com.example.ui_doan3tuan.model

import kotlinx.serialization.Serializable

@Serializable
data class CommentModel(
    val id: Int,
    val post_id: Int,
    val user_id: Int,
    val content: String,
    val status: Int,
    val created_at: String,
    val User: UserModel
)

