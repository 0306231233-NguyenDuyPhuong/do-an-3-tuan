package com.example.ui_doan3tuan.model

import kotlinx.serialization.Serializable

@Serializable
data class ConversationModel(
    val id: Int,
    val type: Int,
    val created_at: String,
    val members: List<ConversationMemberModel>,
    val messages: List<MessageModel>
)

@Serializable
data class ConversationMemberModel(
    val id: Int,
    val conversation_id: Int,
    val user_id: Int,
    val joined_at: String,
    val User: UserModel
)


@Serializable
data class MessageModel(
    val id: Int,
    val conversation_id: Int,
    val sender_id: Int,
    val content: String,
    val created_at: String
)

@Serializable
data class ConversationResponse(
    val message: String,
    val data: List<ConversationModel>
)
