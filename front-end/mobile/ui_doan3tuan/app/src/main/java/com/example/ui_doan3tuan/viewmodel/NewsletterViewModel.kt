package com.example.ui_doan3tuan.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui_doan3tuan.model.CommentModel
import com.example.ui_doan3tuan.model.ListCommentModel
import com.example.ui_doan3tuan.model.ListFriendsModel
import com.example.ui_doan3tuan.model.PostModel
import com.example.ui_doan3tuan.model.PostResponseModel
import com.example.ui_doan3tuan.model.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class NewsletterViewModel : ViewModel() {
    private val client = OkHttpClient()
    private val json: Json = Json { ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _posts = MutableLiveData<List<PostModel>>()
    val posts: LiveData<List<PostModel>> get() = _posts
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error
    fun getPost(token: String,page:Int,limit:Int=10) {
<<<<<<< HEAD

        if (page == 1) {
            _isLoading.postValue(true)
        }

=======
        if (page == 1) {
            _isLoading.postValue(true)
        }
        Log.d("NewLetter", "Vào getPost")
>>>>>>> 0705643a61e733822215bae2f9688a863fa0d6c2
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val req = Request.Builder()
                    .url("http://10.0.2.2:8989/api/posts/users?page=$page&limit=$limit")
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()
<<<<<<< HEAD

                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) {
                        if(resp.code == 403){
                            _error.postValue("TOKEN_EXPIRED")
                        }
                        return@use
                    }
                    val body = resp.body?.string().orEmpty()
                    val response = json.decodeFromString<PostResponseModel>(body)
                    val current = _posts.value ?: emptyList()
                    val newList = response.data
                    val updateList = current + newList
                    Log.d("Test", "Cur $current")
                    Log.d("Test", "New $newList")
                    Log.d("Test", "Update $updateList")
                    _posts.postValue(updateList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
=======
                Log.d("NewLetter", "Token ${token}")
                try {
                    client.newCall(req).execute().use { resp ->
                        if (!resp.isSuccessful) {
                            if (resp.code == 403 || resp.code == 401) {
                                _error.postValue("TOKEN_EXPIRED")
                                Log.d("NewLetter", "Lỗi ${resp.code}")
                            }
                            return@use
                        }
                        val body = resp.body?.string().orEmpty()
                        val response = json.decodeFromString<PostResponseModel>(body)
                        val current = _posts.value ?: emptyList()
                        val newList = response.data
                        val updateList = current + newList
                        Log.d("NewLetter", "Body $body")
                        Log.d("NewLetter", "Cur $current")
                        Log.d("NewLetter", "New $newList")
                        Log.d("NewLetter", "Update $updateList")
                        _posts.postValue(updateList)
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                    Log.d("NewLetter Lỗi", "${e.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("NewLetter Lỗi", "${e.message}")
>>>>>>> 0705643a61e733822215bae2f9688a863fa0d6c2

            }finally {
                _isLoading.postValue(false)

            }
        }
    }


    fun sendComment(postId: Int, content: String,token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("NewLetter", "Goi send comment")
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
                Log.d("NewLetter", "token $token")
                Log.d("NewLetter", "body ${request.body}")
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Log.d("NewLetter", "gửi tn$response")
                    getCommentsByPostId(postId,token)
                }
            } catch (e: Exception) {
                Log.d("NewLetter", "Lỗi ${e.message}")
                e.printStackTrace()
            }
        }


    }
<<<<<<< HEAD
=======
    fun clearComments() {
        _comments.postValue(emptyList())
    }
>>>>>>> 0705643a61e733822215bae2f9688a863fa0d6c2
    fun isLiked(token: String,postId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val commentBody = JSONObject()
                    .put("postId", postId)
                    .toString()
                val JSON = "application/json;charset=utf-8".toMediaType();
                val requestBody = commentBody.toRequestBody(JSON);
                val request = Request.Builder()
                    .url("http://10.0.2.2:8989/api/interact/like")
                    .addHeader("Authorization", "Bearer $token")
                    .post(requestBody)
                    .build()
                Log.d("Like", "$token")
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Log.d("Like", "$response")
                }else{
                    Log.d("Like", "${response.code}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Like", "${e.message}")
            }
        }


    }

    private val _comments = MutableLiveData<List<CommentModel>>()
    val comments: LiveData<List<CommentModel>> get() = _comments
    fun getCommentsByPostId(postId: Int, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("NewLetter", "Goi get comment postId: $postId")
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:8989/api/comments/$postId")
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()

                client.newCall(request).execute().use { resp ->
                    if (resp.isSuccessful) {
                        val jsonString = resp.body?.string()
                        Log.d("NewLetter", "Response Comment: $jsonString") // Log để kiểm tra JSON trả về
                        // Sửa chỗ này: Nếu jsonString null thì trả về chuỗi json rỗng hợp lệ với model
                        // Giả sử ListCommentModel có cấu trúc { data: [] }
                        try {
                            val listComment = json.decodeFromString<ListCommentModel>(jsonString ?: "{\"data\":[]}")
                            _comments.postValue(listComment.data)
                        } catch (e: Exception) {
                            Log.e("NewLetter", "Lỗi Parse JSON Comment: ${e.message}")
                        }
                    } else {
                        Log.e("NewLetter", "Lỗi API Get Comment: code ${resp.code}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("NewLetter", "Lỗi Crash Get Comment: ${e.message}")
            }
        }
    }
    private val _listFriends = MutableLiveData<List<UserModel>>()
    val friends: LiveData<List<UserModel>> get() = _listFriends
    fun getListfriends(token:String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:8989/api/friends")
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()

                client.newCall(request).execute().use {
                        resp->
                    if (resp.isSuccessful) {
                        val jsonString = resp.body?.string()
                        val listFriendsModel = json.decodeFromString<ListFriendsModel>(jsonString ?: "[]")
                        _listFriends.postValue(listFriendsModel.data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
    private val _report = MutableLiveData<Boolean>()
    val report: LiveData<Boolean> get() = _report
    fun reportPost(token: String, postId: Int,userId:Int,reason:String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val reportBody = JSONObject()
                    .put("reporter_id", userId)
                    .put("target_id", postId)
                    .put("target_type", "post")
                    .put("reason", reason)
                    .put("description", "sd")
                    .toString()
                val JSON = "application/json;charset=utf-8".toMediaType();
                val requestBody = reportBody.toRequestBody(JSON);
                val request = Request.Builder()
                    .url("http://10.0.2.2:8989/api/reports")
                    .addHeader("Authorization", "Bearer $token")
                    .post(requestBody)
                    .build()
                client.newCall(request).execute().use {
                        resp->
                    if (resp.isSuccessful) {
                        _report.postValue(true)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}