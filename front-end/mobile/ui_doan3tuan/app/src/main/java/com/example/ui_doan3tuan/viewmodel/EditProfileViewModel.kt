package com.example.ui_doan3tuan.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

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
//                    val response = json.decodeFromString<PostResponseModel>(jsonBody)
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