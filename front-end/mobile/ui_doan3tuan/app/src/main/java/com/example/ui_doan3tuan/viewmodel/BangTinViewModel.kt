package com.example.ui_doan3tuan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui_doan3tuan.model.PostModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class BangTinViewModel : ViewModel() {
    private val client = OkHttpClient()
    private val json: Json = Json { ignoreUnknownKeys = true }

    // Dùng LiveData để lưu danh sách bài đăng
    private val _posts = MutableLiveData<List<PostModel>>()
    val posts: LiveData<List<PostModel>> get() = _posts
    // Dùng LiveData để báo lỗi nếu cần
    val error = MutableLiveData<String>()

    fun getProduct(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:8989/api/posts/users")
                    .get()
                    .addHeader("Authorization", "Bearer $token")
                    .build()

                client.newCall(request).execute().use { response ->
                    val bodyJson = response.body?.string() ?: ""
                    if (!response.isSuccessful) {
                        error.postValue("Lỗi: ${response.code}")
                        return@use
                    }
                    val data = json.decodeFromString<List<PostModel>>(bodyJson)
                    _posts.postValue(data);
                }
            } catch (e: Exception) {
                error.postValue(e.message)
            }
        }
    }
}