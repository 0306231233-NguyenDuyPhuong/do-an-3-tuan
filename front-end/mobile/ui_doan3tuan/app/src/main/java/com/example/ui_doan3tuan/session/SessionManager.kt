package com.example.ui_doan3tuan.session


import android.content.Context
import android.content.SharedPreferences
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

            putInt("user_id", user.id)
            putString("user_role", user.role)
            putString("full_name", user.full_name)
            putString("email", user.email)
            putString("phone", user.phone)

            putBoolean("is_logged_in", true)
            apply()
        }
    }

    fun getAccessToken(): String? =
        pref.getString("access_token", null)

    fun getRefreshToken(): String? =
        pref.getString("refresh_token", null)

    fun getUser(): User? {
        val id = pref.getInt("user_id", -1)
        if (id == -1) return null

        return User(
            id = id,
            email = pref.getString("email", null),
            phone = pref.getString("phone", null),
            full_name = pref.getString("full_name", "")?: "",
            role = pref.getString("user_role", "")?: ""
        )
    }

    fun isLoggedIn(): Boolean =
        pref.getBoolean("is_logged_in", false)
                && getAccessToken() != null

    fun clearSession() {
        pref.edit().clear().apply()
    }
}
