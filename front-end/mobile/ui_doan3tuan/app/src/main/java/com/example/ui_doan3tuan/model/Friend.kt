package com.example.ui_doan3tuan.model

// bạn bè
data class Friend(
    val id: Int,
    val full_name: String,
    val avatar: String
)

data class FriendListResponse(
    val data: List<Friend>
)

// lời mời kết bạn
data class FriendRequest(
    val id: Int,
    val sender: UserSender
)

data class UserSender(
    val id: Int,
    val full_name: String,
    val avatar: String?
)

data class FriendRequestResponse(
    val data: List<FriendRequest>
)

// Model cho accept/reject request
data class AcceptRequest(
    val from: Int
)

data class RejectRequest(
    val from: Int
)

//
data class ApiMessage(
    val message: String
)
