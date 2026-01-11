package com.example.ui_doan3tuan.model

import kotlinx.serialization.Serializable

@Serializable
data class PostResponse(
    val message: String,
    val data: List<PostModel> // Nếu JSON trả về 1 object thì để PostModel, nếu trả về danh sách thì để List<PostModel>
)

