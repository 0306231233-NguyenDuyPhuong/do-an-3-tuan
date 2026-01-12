// Tạo file UserSearchResult.kt
package com.example.ui_doan3tuan.model

// Model cho kết quả tìm kiếm
data class UserSearchResult(
    val id: Int,
    val full_name: String,
    val email: String,
    val avatar: String
)

// Model cho response tìm kiếm
data class UserSearchResponse(
    val data: List<UserSearchResult>,
    val total: Int
)