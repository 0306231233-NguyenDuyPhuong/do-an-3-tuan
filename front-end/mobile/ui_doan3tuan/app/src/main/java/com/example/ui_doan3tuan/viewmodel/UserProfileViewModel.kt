package com.example.ui_doan3tuan.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui_doan3tuan.model.CommentModel
import com.example.ui_doan3tuan.model.LikedPostModel
import com.example.ui_doan3tuan.model.ListCommentModel
import com.example.ui_doan3tuan.model.PostModel
import com.example.ui_doan3tuan.model.PostResponseIDModel
import com.example.ui_doan3tuan.model.PostResponseModel
import com.example.ui_doan3tuan.model.SharePostModel
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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _postsId = MutableLiveData<List<PostModel>>()
    val postsId: LiveData<List<PostModel>> get() = _postsId
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getPostID(token: String, id: Int) {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val req = Request.Builder()
                    .url("http://10.0.2.2:8989/api/users/$id")
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()
                Log.e("Get Post ID", "Vào 1")
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) {
                        Log.e("Get Post ID", "Lỗi: ${resp.code}")
                        if (resp.code == 401||resp.code == 403) {
                            _error.postValue("TOKEN_EXPIRED")
                            return@use
                        }
                    }
                    val jsonBody = resp.body?.string().orEmpty()
                    val response = json.decodeFromString<PostResponseIDModel>(jsonBody)
                    val listPostId = response.post
                    slbv = response.post_count
                    slbb = response.friend_count
                    Log.d("Get Post ID", "$slbv")
                    Log.d("Get Post ID", "$slbb")

                    Log.d("Get Post ID", "$listPostId")
                    _postsId.postValue(listPostId)
                }

            } catch (e: Exception) {
                Log.e("Get Post ID", "Lỗi mạng: ${e.message}")
                _error.postValue("Kết nối mạng không ổn định, vui lòng thử lại sau!")
            }finally {
                _isLoading.postValue(false)
            }
        }
    }



    fun getListPostSave(token: String) {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val req = Request.Builder()
                    .url("http://10.0.2.2:8989/api/interact/saved")
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()
                Log.e("ListSavePost", "Vào 1")
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) {
                        Log.e("API_ERROR", "Lỗi: ${resp.code}")
                        if (resp.code == 401||resp.code == 403) {
                            _error.postValue("TOKEN_EXPIRED")
                            return@use
                        }
                    }
                    val jsonBody = resp.body?.string().orEmpty()
                    val response = json.decodeFromString<SharePostModel>(jsonBody)
                    val listPostId = response.data
                    val listPost: MutableList<PostModel> = mutableListOf()
                    for (post in listPostId) {
                        listPost.add(post.Post)
                    }
                    Log.d("ListSavePost", "listPostId $listPostId")
                    Log.d("ListSavePost", "listPost $listPost")

                    _postsId.postValue(listPost)
                }

            } catch (e: Exception) {
                Log.e("ListSavePost", "Lỗi mạng: ${e.message}")
                _error.postValue("Kết nối mạng không ổn định, vui lòng thử lại sau!")
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun getListPostLike(token: String) {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val req = Request.Builder()
                    .url("http://10.0.2.2:8989/api/interact/post/like")
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()
                Log.e("ListLikePost", "Vào 1")
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) {
                        Log.e("API_ERROR", "Lỗi: ${resp.code}")
                        if (resp.code == 401||resp.code == 403) {
                            _error.postValue("TOKEN_EXPIRED")
                            return@use
                        }
                    }
                    val jsonBody = resp.body?.string().orEmpty()
                    val response = json.decodeFromString<LikedPostModel>(jsonBody)
                    val listPostId = response.data
                    val listPost: MutableList<PostModel> = mutableListOf()
                    for (post in listPostId) {
                        listPost.add(post.Post)
                    }
                    Log.d("ListLikePost", "listPostId $listPostId")
                    Log.d("ListLikePost", "listPost $listPost")

                    _postsId.postValue(listPost)
                }

            } catch (e: Exception) {
                Log.e("ListLikePost", "Lỗi mạng: ${e.message}")
                _error.postValue("Kết nối mạng không ổn định, vui lòng thử lại sau!")
            }finally {
                _isLoading.postValue(false)
            }
        }
    }


    private val _deletePost = MutableLiveData<Boolean>()
    val deletePost: LiveData<Boolean> get() = _deletePost
    fun deletePost(postId: Int,token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val commentBody = JSONObject()
                    .put("status", 0)
                    .toString()
                val JSON = "application/json;charset=utf-8".toMediaType();
                val requestBody = commentBody.toRequestBody(JSON);
                Log.e("Lỗi", "Id lần 2: ${postId}")
                val request = Request.Builder()
                    .url("http://10.0.2.2:8989/api/posts/$postId")
                    .addHeader("Authorization", "Bearer $token")
                    .delete(requestBody)
                    .build()
                val response = client.newCall(request).execute()
                Log.e("Lỗi", "body: ${response.code}")
                Log.e("Lỗi", "body: ${response.body}")
                if (response.isSuccessful) {
                    Log.e("Lỗi", "Thành công")
                    _deletePost.postValue(true)
                }else{
                    Log.e("Lỗi", "Vô1")
                    Log.e("Lỗi", "body: ${response.message}")
                    _deletePost.postValue(false)
                }
            } catch (e: Exception) {
                Log.e("Lỗi", "Lỗi mạng: ${e.message}")
                e.printStackTrace()
            }
        }


    }
    private val _unSavePost = MutableLiveData<Boolean>()
    val unSavePost: LiveData<Boolean> get() = _unSavePost
    fun unSavePost(token: String,postId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val commentBody = JSONObject()
                    .put("postId", postId)
                    .toString()
                val JSON = "application/json;charset=utf-8".toMediaType();
                val requestBody = commentBody.toRequestBody(JSON);
                Log.e("ListSavePost", "Id lần 2: ${postId}")
                val request = Request.Builder()
                    .url("http://10.0.2.2:8989/api/interact/unsave")
                    .addHeader("Authorization", "Bearer $token")
                    .delete(requestBody)
                    .build()
                val response = client.newCall(request).execute()
                Log.e("ListSavePost", "body: ${response.code}")
                Log.e("ListSavePost", "body: ${response.body}")
                if (response.isSuccessful) {
                    Log.e("ListSavePost", "Thành công")
                    _unSavePost.postValue(true)
                }else{
                    Log.e("ListSavePost", "Vô1")
                    Log.e("ListSavePost", "body: ${response.message}")
                    _unSavePost.postValue(false)
                }
            } catch (e: Exception) {
                Log.e("ListSavePost", "Lỗi mạng: ${e.message}")
                e.printStackTrace()
            }
        }


    }

}