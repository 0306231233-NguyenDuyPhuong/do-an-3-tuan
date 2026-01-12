package com.example.ui_doan3tuan.model

import kotlinx.serialization.Serializable

@Serializable
data class PostResponseModel(
    val message: String,
    val data: List<PostModel>
)

