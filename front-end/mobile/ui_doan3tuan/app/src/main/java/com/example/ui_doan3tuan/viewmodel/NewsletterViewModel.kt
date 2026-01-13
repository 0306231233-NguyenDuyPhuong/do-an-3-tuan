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

        if (page == 1) {
            _isLoading.postValue(true)
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val req = Request.Builder()
                    .url("http://10.0.2.2:8989/api/posts/users?page=$page&limit=$limit")
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()

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

            }finally {
                _isLoading.postValue(false)

            }
        }
    }


    fun sendComment(postId: Int, content: String,token: String) {
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
                    Log.d("Test", "$response")
                    getCommentsByPostId(postId,token)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }
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
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:8989/api/comments/$postId")
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()
                client.newCall(request).execute().use {
                    resp->
                    if (resp.isSuccessful) {
                        val jsonString = resp.body?.string()
                        Log.d("Test", "$jsonString")
                        val listComment = json.decodeFromString<ListCommentModel>(jsonString ?: "[]")
                        _comments.postValue(listComment.data)
                    }
                }


          } catch (e: Exception) {
                e.printStackTrace()
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