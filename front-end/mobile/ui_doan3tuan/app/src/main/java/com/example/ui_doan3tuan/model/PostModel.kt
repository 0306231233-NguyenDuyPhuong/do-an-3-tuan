package com.example.ui_doan3tuan.model

data class PostModel(
    var id: Int,
    var content: String,
    var private:String,
    var status: String,
    var created_at: String,
    var updated_at: String,
    var location_id: String,
    var user_id: Int,
    var like_count: Int,
    var comment_count: Int,
//    var share_count: Int,
    var user: UserModel,
    var media: List<PostMediaModel>

)
