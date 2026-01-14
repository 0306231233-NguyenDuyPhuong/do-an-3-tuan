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
import com.example.ui_doan3tuan.model.ResponseSearchModel
import com.example.ui_doan3tuan.model.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlin.collections.plus

class SearchViewModel: ViewModel() {
    private val client = OkHttpClient()
    private val json: Json = Json { ignoreUnknownKeys = true}

    private val _resultSearch = MutableLiveData<List<PostModel>>()
    val resultSearch: LiveData<List<PostModel>> get() = _resultSearch
    private val _error = MutableLiveData<String>()

    fun searchContent(token: String,page:Int,content:String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("Search", "Vô searchContent")
            try {
                val req = Request.Builder()
                    .url("http://10.0.2.2:8989/api/posts/users?page=$page&search=$content")
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()
                Log.d("Search", "$req")
                client.newCall(req).execute().use { resp ->
                    Log.d("Search", "Vô client")
                    if (resp.isSuccessful) {
                        val body = resp.body?.string().orEmpty()
                        val response = json.decodeFromString<ResponseSearchModel>(body)
                        val current = _resultSearch.value ?: emptyList()
                        val newList = response.data
                        val updateList =if(page==1) newList else current + newList
                        Log.d("Search", "Cur $current")
                        Log.d("Search", "New $newList")
                        Log.d("Search", "Update $updateList")
                        Log.d("Search", "Update ${updateList.size}")
                        _resultSearch.postValue(updateList)
                    }else{
                        Log.d("Search", "Lỗi")
                        Log.d("Search", "${resp.code}")
                        Log.d("Search", "${resp.message}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Search", "${e.message}")
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
    fun likePost(token: String,postId: Int) {
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
    fun UnlikePost(token: String,postId: Int) {
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
                    .delete(requestBody)
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