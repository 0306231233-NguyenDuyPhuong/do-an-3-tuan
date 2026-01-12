package com.example.ui_doan3tuan.model

import kotlinx.serialization.Serializable

@Serializable
data class ListFriends(
    var data:List<UserModel>
)