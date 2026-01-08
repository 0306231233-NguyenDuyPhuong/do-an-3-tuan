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
    var created_at: String,
    var updated_at: String,
    @SerialName("location_id") var locationId: Int?,
    @SerialName("user_id") var userId: Int,
    var User: UserModel,
    var Location:LocationModel,
    var PostMedia: List<PostMediaModel>

)
@Serializable
data class PostResponse(
    val message: String,
    val data: List<PostModel> // Nếu JSON trả về 1 object thì để PostModel, nếu trả về danh sách thì để List<PostModel>
)
