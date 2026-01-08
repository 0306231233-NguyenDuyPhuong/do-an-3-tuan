package com.example.ui_doan3tuan.model
import kotlinx.serialization.Serializable


@Serializable
data class LocationModel(
    var id:Int,
    var name:String,
    var address:String,
    var deleted_at:String?,
    var created_at:String,
    var updated_at:String,
)
