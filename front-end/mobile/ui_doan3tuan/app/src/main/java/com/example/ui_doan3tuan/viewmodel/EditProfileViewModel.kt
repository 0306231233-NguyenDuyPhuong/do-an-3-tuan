package com.example.ui_doan3tuan.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui_doan3tuan.model.PostResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class EditProfileViewModel: ViewModel() {
    private val client = OkHttpClient()
    private val json: Json = Json { ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true }
//    fun editProFile(token:String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val jsonBody = JSONObject()
//                    .put("full_name","")
//                    .put("phone","")
//                    .put("avatar","")
//                    .toString()
//                val req = Request.Builder()
//                    .url("http://10.0.2.2:8989/api/user/update")
//                    .addHeader("Authorization", "Bearer $token")
//                    .patch()
//                    .build()
//
//                client.newCall(req).execute().use { resp ->
//                    val jsonBody = resp.body?.string().orEmpty()
//                    val response = json.decodeFromString<PostResponse>(jsonBody)
//                    val list = response.data
//
//                }
//
//            } catch (e: Exception) {
//
//            }
//        }
//    }
}