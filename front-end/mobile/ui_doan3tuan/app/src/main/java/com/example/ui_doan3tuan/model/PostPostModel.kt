package com.example.ui_doan3tuan.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class PostPostModel(
    var id: Int,
    @SerialName("user_id") var userId: Int,
    var content: String,
    var privacy:Int,
    @SerialName("location_id") var locationId: Int?,
    var status:Int,
    @SerialName("updated_at") var updatedAt: String,
    @SerialName("created_at") var createdAt: String,
)
@Serializable
data class ResponsePostPostModel(
    val message: String,
    val data:PostPostModel
)

