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
@Serializable
data class PostResponse(
    val message: String,
    val data: List<PostModel> // Nếu JSON trả về 1 object thì để PostModel, nếu trả về danh sách thì để List<PostModel>
)

@Serializable
data class CommentRequest(
    val post_id: Int,
    val content: String
    // user_id thường lấy từ token hoặc session, nếu API cần thì thêm vào
)
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
@Serializable
data class ListCommentModel(
    var data:List<CommentModel>
)
