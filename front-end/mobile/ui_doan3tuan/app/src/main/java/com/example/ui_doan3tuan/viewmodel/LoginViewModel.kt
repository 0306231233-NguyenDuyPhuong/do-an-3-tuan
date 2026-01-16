package com.example.ui_doan3tuan.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ui_doan3tuan.ApiClient
import com.example.ui_doan3tuan.model.LoginRequest
import com.example.ui_doan3tuan.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    fun login(
        username: String,
        password: String,
        onSuccess: (LoginResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        if (username.isBlank() || password.isBlank()) {
            onError("Vui lòng nhập đầy đủ thông tin")
            return
        }

        val request = LoginRequest(username, password)

        ApiClient.apiService.login(request)
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!

                        if (!data.accessToken.isNullOrEmpty() &&
                            !data.refreshToken.isNullOrEmpty()
                        ) {
                            onSuccess(data)
                        } else {
                            onError("Token không hợp lệ")
                        }
                    } else {
                        onError("Sai thông tin đăng nhập")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    onError("Lỗi mạng: ${t.message}")
                }
            })
    }
}
