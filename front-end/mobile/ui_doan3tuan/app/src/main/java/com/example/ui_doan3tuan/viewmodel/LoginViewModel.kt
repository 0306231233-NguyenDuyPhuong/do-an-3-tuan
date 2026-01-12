package com.example.ui_doan3tuan.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.ui_doan3tuan.ApiClient
import com.example.ui_doan3tuan.model.LoginRequest
import com.example.ui_doan3tuan.model.LoginResponse
import com.example.ui_doan3tuan.model.User

class LoginViewModel : ViewModel() {

    var isLoading = false
    var errorMessage = ""
    private lateinit var sharedPref: SharedPreferences

    fun init(context: Context) {
        sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
    }

    // Hàm đăng nhập
    fun login(username: String, password: String,
              onSuccess: (accessToken: String, refreshToken: String,
                          userData: User) -> Unit,
              onError: (error: String) -> Unit) {

        if (username.isEmpty() || password.isEmpty()) {
            onError("Vui lòng nhập đầy đủ thông tin")
            return
        }

        isLoading = true
        errorMessage = ""

        val request = LoginRequest(username, password)

        ApiClient.apiService.login(request).enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(
                call: retrofit2.Call<LoginResponse>,
                response: retrofit2.Response<LoginResponse>
            ) {
                isLoading = false

                when (response.code()) {
                    200 -> {
                        val data = response.body()
                        if (data != null) {
                            saveLoginData(data)
                            onSuccess(data.accessToken, data.refreshToken, data.user)
                        } else {
                            onError("Không nhận được dữ liệu từ server")
                        }
                    }
                    400 -> onError("Vui lòng nhập đầy đủ thông tin")
                    401 -> onError("Sai thông tin đăng nhập")
                    500 -> onError("Lỗi server, vui lòng thử lại sau")
                    else -> onError("Lỗi không xác định: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                isLoading = false
                onError("Lỗi kết nối mạng: ${t.message ?: "Không xác định"}")
            }
        })
    }

    // Lưu dữ liệu đăng nhập
    private fun saveLoginData(response: LoginResponse) {
        val currentTime = System.currentTimeMillis()
        val editor = sharedPref.edit()

        // Lưu tokens với thời gian
        editor.putString("access_token", response.accessToken)
        editor.putLong("access_token_time", currentTime)

        editor.putString("refresh_token", response.refreshToken)
        editor.putLong("refresh_token_time", currentTime)

        editor.putInt("user_id", response.user.id)
        editor.putString("user_role", response.user.role)
        editor.putString("full_name", response.user.full_name)
        editor.apply()

        if (response.user.email != null) {
            editor.putString("email", response.user.email)
        }
        if (response.user.phone != null) {
            editor.putString("phone", response.user.phone)
        }

        // Lưu trạng thái đã đăng nhập
        editor.putBoolean("is_logged_in", true)
        editor.putLong("last_login_time", currentTime)

        editor.apply()
    }

    // KIỂM TRA TOKEN CÒN HIỆU LỰC
    fun isTokenValid(): Boolean {
        val accessTime = sharedPref.getLong("access_token_time", 0)
        if (accessTime != 0L) {
            val minutes = (System.currentTimeMillis() - accessTime) / (60 * 1000)
            if (minutes < 10) return true  // Access token còn hiệu lực
        }

        // Nếu access hết hạn, check refresh
        val refreshTime = sharedPref.getLong("refresh_token_time", 0)
        if (refreshTime == 0L) return false

        val days = (System.currentTimeMillis() - refreshTime) / (24 * 60 * 60 * 1000)
        return days < 7
    }

    fun isLoggedIn(): Boolean {
        // Kiểm tra có token không
        val hasToken = sharedPref.getString("access_token", null) != null
        val isLoggedInFlag = sharedPref.getBoolean("is_logged_in", false)

        return hasToken && isLoggedInFlag
    }


}