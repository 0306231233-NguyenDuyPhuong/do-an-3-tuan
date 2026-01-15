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
    var isLastPage = false
    var currentQuery = ""

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading
    fun clearSearchData() {
        _resultSearch.value = emptyList()
        _isLoading.value = false
    }
    fun searchContent(token: String,page:Int,content:String) {
        if (_isLoading.value == true) return
        currentQuery = content
        if(page==1){
            _isLoading.postValue(false)
        }

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
                        val newList = response.data ?: emptyList()
                        if (newList.isEmpty()) {
                            isLastPage = true
                        }
                        val currentList = if (page == 1) {
                            emptyList()
                        } else {
                            _resultSearch.value ?: emptyList()
                        }
                        val updatedList = currentList + newList
                        _resultSearch.postValue(updatedList)
                        Log.d("Search", "tanh cong")
                        if (newList.isEmpty() && page == 1) {
                            _error.postValue("Không tìm thấy kết quả")
                        }
                    }else{
                        Log.d("Search", "Lỗi")
                        Log.d("Search", "${resp.code}")
                        Log.d("Search", "${resp.message}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Search", "${e.message}")
            }finally {
                _isLoading.postValue(false)
            }
        }
    }

}