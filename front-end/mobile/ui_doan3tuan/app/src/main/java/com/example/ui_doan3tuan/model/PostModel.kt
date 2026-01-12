package com.example.ui_doan3tuan.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PostModel(
    var id: Int,
    var content: String,
    var privacy:Int,
    var status: Int,
    @SerialName("like_count") var likeCount: Int=0,
    @SerialName("comment_count")var commentCount: Int=0,
    @SerialName("share_count") var shareCount: Int=0,
    @SerialName("created_at") var createdAt: String,
    @SerialName("updated_at") var updatedAt: String,
    @SerialName("location_id") var locationId: Int?,
    @SerialName("user_id") var userId: Int,
    var User: UserModel,
    var Location:LocationModel,
    var PostMedia: List<PostMediaModel>
)

