package com.example.ui_doan3tuan.model


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("email")
    val email: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("role")
    val role: String
)

data class AuthResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("user")
    val user: User
)

data class LoginRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val authResponse: AuthResponse) : LoginState()
    data class Error(val message: String, val code: Int? = null) : LoginState()
}

data class LoginFormState(
    val username: String = "",
    val password: String = "",
    val isFormValid: Boolean = false,
    val usernameError: String? = null,
    val passwordError: String? = null
)