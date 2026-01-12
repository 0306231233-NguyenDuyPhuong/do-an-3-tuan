package com.example.ui_doan3tuan.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PostModel(
    var id: Int,
    var content: String,
    var privacy:Int,
    var status: Int,
    @SerialName("like_count") var likeCount: Int,
    @SerialName("comment_count")var commentCount: Int,
    @SerialName("share_count") var shareCount: Int,
    @SerialName("created_at") var createdAt: String,
    @SerialName("updated_at") var updatedAt: String,
    @SerialName("location_id") var locationId: Int?,
    @SerialName("user_id") var userId: Int,
    var User: UserModel,
    var Location:LocationModel,
    var PostMedia: List<PostMediaModel>

)

