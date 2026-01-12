package com.example.ui_doan3tuan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class CreatePostViewModel: ViewModel() {
    private val client = OkHttpClient()
    private val json: Json = Json { ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true }
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