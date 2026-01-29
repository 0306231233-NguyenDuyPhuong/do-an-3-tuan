package com.example.ui_doan3tuan.model
data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val rePassword: String,
    val fullname: String
)

data class LoginResponse(
    val message: String,
    val accessToken: String,
    val refreshToken: String,
    val user: User
)

data class RegisterResponse(
    val message: String,
    val user: User
)

data class User(
    val id: Int,
    val email: String?,
    val phone: String?,
    var full_name: String,
    val role: String,
    var avatar:String?
)

data class ErrorResponse(
    val message: String
)

data class ForgotPasswordRequest(
    val username: String
)

data class ForgotPasswordResponse(
    val message: String
)