package com.example.ui_doan3tuan.model

import kotlinx.serialization.Serializable

@Serializable
data class ListFriendsModel(
    var data:List<UserModel>
)