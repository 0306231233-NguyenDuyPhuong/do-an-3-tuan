package com.example.ui_doan3tuan.model

import kotlinx.serialization.Serializable


@Serializable
data class UserModel(
    var id:Int,
    var full_name:String?,
    var avatar:String?,
    var status:Int
)
