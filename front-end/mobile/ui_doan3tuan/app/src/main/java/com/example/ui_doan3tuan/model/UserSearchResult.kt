// Tạo file UserSearchResult.kt
package com.example.ui_doan3tuan.model

import kotlinx.serialization.Serializable
@Serializable
// Model cho kết quả tìm kiếm
data class UserSearchResult(
    val id: Int,
    val full_name: String,
    val email: String,
    val avatar: String
)

@Serializable
// Model cho response tìm kiếm
data class UserSearchResponse(
    val data: List<UserSearchResult>,
    val total: Int
)
@Serializable
data class ResponseSearchModel(
    val message: String,
    val total: Int,
    val page:Int,
    val limit: Int,
    val data: List<PostModel>
)