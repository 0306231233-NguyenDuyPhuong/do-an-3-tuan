package com.example.ui_doan3tuan.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui_doan3tuan.model.CommentModel
import com.example.ui_doan3tuan.model.ListCommentModel
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

class UserProfileViewModel: ViewModel() {
    private val client = OkHttpClient()
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }


    private val _postsId = MutableLiveData<List<PostModel>>()
    val postsId: LiveData<List<PostModel>> get() = _postsId
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getPostID(token: String, id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val req = Request.Builder()
                    .url("http://10.0.2.2:8989/api/users/$id")
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()
                Log.e("Lỗi", "Vào 1")
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) {
                        Log.e("API_ERROR", "Lỗi: ${resp.code}")
                        if (resp.code == 401) {
                            _error.postValue("Phiên đăng nhập hết hạn, vui lòng login lại.")
                            return@use
                        }
                    }
                    val jsonBody = resp.body?.string().orEmpty()

                    val response = json.decodeFromString<PostResponseIDModel>(jsonBody)
                    val listPostId = response.post
                    slbv = response.post_count
                    slbb = response.friend_count
                    Log.d("Test", "$slbv")
                    Log.d("Test", "$slbb")

                    Log.d("listPostId", "$listPostId")
                    _postsId.postValue(listPostId)
                }

            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Lỗi mạng: ${e.message}")
                _error.postValue("Kết nối mạng không ổn định, vui lòng thử lại sau!")
            }
        }
    }

    fun sendComment(postId: Int, content: String, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val commentBody = JSONObject()
                    .put("postId", postId)
                    .put("content", content)
                    .toString()
                val JSON = "application/json;charset=utf-8".toMediaType();
                val requestBody = commentBody.toRequestBody(JSON);
                val request = Request.Builder()
                    .url("http://10.0.2.2:8989/api/comment")
                    .addHeader("Authorization", "Bearer $token")
                    .post(requestBody)
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    getCommentsByPostId(postId,token)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    private val _comments = MutableLiveData<List<CommentModel>>()
    val comments: LiveData<List<CommentModel>> get() = _comments
    fun getCommentsByPostId(postId: Int, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:8989/api/comments/$postId")
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()
                client.newCall(request).execute().use { resp ->
                    if (resp.isSuccessful) {
                        val jsonString = resp.body?.string()
                        val listComment =
                            json.decodeFromString<ListCommentModel>(jsonString ?: "[]")
                        Log.d("Test", "$listComment")
                        Log.d("Test", "$postId")
                        _comments.postValue(listComment.data)
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

