package com.example.ui_doan3tuan.model

// bạn bè
data class Friend(
    val id: Int,
    val full_name: String,
    val avatar: String
)

data class FriendListResponse(
    val data: List<Friend>? = null,
    val message: String? = null,
    val success: Boolean? = null
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

// Model cho gửi lời mời
data class SendRequest(
    val to: Int
)

// Model cho hủy kết bạn
data class UnfriendRequest(
<<<<<<< HEAD
    val friendId: Int,
)
data class CancelRequest(
    val to: Int
)

=======
    val friendId: Int
)


>>>>>>> 0705643a61e733822215bae2f9688a863fa0d6c2
// Response message
data class ApiMessage(
    val message: String
)