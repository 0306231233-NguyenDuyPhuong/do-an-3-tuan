package com.example.ui_doan3tuan.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui_doan3tuan.model.PostModel
import com.example.ui_doan3tuan.model.PostResponseIDModel
import com.example.ui_doan3tuan.view.slbb
import com.example.ui_doan3tuan.view.slbv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LogoutViewModel: ViewModel() {
    private val client = OkHttpClient()
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }
    private val _logout = MutableLiveData<Boolean>()
    val logout: LiveData<Boolean> get() = _logout
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error
    fun logout(token: String, refreshToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val commentBody = JSONObject()
                    .put("refreshToken", refreshToken)
                    .toString()
                val JSON = "application/json;charset=utf-8".toMediaType();
                val requestBody = commentBody.toRequestBody(JSON);
                val request = Request.Builder()
                    .url("http://10.0.2.2:8989/api/auth/logout")
                    .addHeader("Authorization", "Bearer $token")
                    .post(requestBody)
                    .build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    Log.d("Test", "Đăng xuất thành công")

                    _logout.postValue(true)
                }
                else{
                    Log.d("Test", "Đăng xuất thất bại")
                    Log.d("Test", "${response.code}")
                    Log.d("Test", "${response.body}")
                }


            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Lỗi mạng: ${e.message}")
                _error.postValue("Kết nối mạng không ổn định, vui lòng thử lại sau!")
            }
        }
    }
}