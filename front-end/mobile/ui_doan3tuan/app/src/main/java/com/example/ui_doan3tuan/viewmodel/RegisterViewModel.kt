package com.example.ui_doan3tuan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ui_doan3tuan.ApiClient
import com.example.ui_doan3tuan.model.RegisterRequest
import com.example.ui_doan3tuan.model.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    private val _dangLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _dangLoading
    private val _dangKyThanhCong = MutableLiveData<RegisterResponse>()
    val registerSuccess: LiveData<RegisterResponse> = _dangKyThanhCong
    private val _loi = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _loi
    fun register(email: String, matKhau: String, nhapLaiMatKhau: String, hoTen: String) {
        // Đang loading
        _dangLoading.value = true
        // Tạo request
        val request = RegisterRequest(email, matKhau, nhapLaiMatKhau, hoTen)

        ApiClient.apiService.register(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {

                _dangLoading.value = false

                if (response.isSuccessful) {

                    val ketQua = response.body()

                    if (ketQua != null) {
                        // Thành công
                        _dangKyThanhCong.value = ketQua
                    } else {

                        _loi.value = "Không nhận được dữ liệu"
                    }
                } else {
                    // Xử lý lỗi từ server
                    val maLoi = response.code()

                    when (maLoi) {
                        400 -> _loi.value = "Thông tin không hợp lệ"
                        409 -> _loi.value = "Email/số điện thoại đã tồn tại"
                        500 -> _loi.value = "Lỗi máy chủ"
                        else -> _loi.value = "Lỗi không xác định: $maLoi"
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                // Xong loading
                _dangLoading.value = false

                // Lỗi kết nối
                _loi.value = "Lỗi kết nối: ${t.message}"
            }
        })
    }

    // Reset trạng thái
    fun reset() {
        _loi.value = ""
    }
}