package com.example.ui_doan3tuan.model


import kotlinx.serialization.Serializable


@Serializable
data class PostMediaModel(
    val id:Int,
    val media_url:String?,
    val media_type:Int,
    val thumbnail_url:String?,
    val deleted_at:String?,
    val created_at:String,
    val updated_at:String,
    val post_id:Int,
)
