package com.example.ui_doan3tuan.session


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.ui_doan3tuan.model.User

class SessionManager(context: Context) {

    private val pref: SharedPreferences =
        context.getSharedPreferences("user_data", Context.MODE_PRIVATE)

    fun saveSession(
        accessToken: String,
        refreshToken: String,
        user: User
    ) {
        pref.edit().apply {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            Log.d("Logout", "accessTokenSession: $accessToken")
            Log.d("Logout", "refreshTokenSession: $refreshToken")
            putInt("user_id", user.id)
            putString("user_role", user.role)
            putString("full_name", user.full_name)
            putString("email", user.email)
            putString("phone", user.phone)
            putString("avatar", user.avatar)

            putBoolean("is_logged_in", true)
            apply()
        }
    }

    fun getAccessToken(): String? =
        pref.getString("access_token", null)

    fun getRefreshToken(): String? =
        pref.getString("refresh_token", null)

    fun getUser(): User {
        val id = pref.getInt("user_id", -1)
        if (id == -1) return User(-1, "", "", "", "","")

        return User(
            id = id,
            email = pref.getString("email", null),
            phone = pref.getString("phone", null),
            full_name = pref.getString("full_name", "")?: "",
            role = pref.getString("user_role", "")?: "",
            avatar = pref.getString("avatar", null)?:""
        )
    }
    fun updateAvatar(newAvatarUrl: String) {
        pref.edit().putString("avatar", newAvatarUrl).apply()
    }
    fun updateUserName(newUserName: String) {
        pref.edit().putString("full_name", newUserName).apply()
    }

    fun isLoggedIn(): Boolean =
        pref.getBoolean("is_logged_in", false)
                && getAccessToken() != null

    fun clearSession() {
        pref.edit().clear().apply()
    }
}
