package com.example.ui_doan3tuan.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui_doan3tuan.model.PostPostModel
import com.example.ui_doan3tuan.model.PostResponseModel
import com.example.ui_doan3tuan.model.ResponsePostPostModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

class CreatePostViewModel : ViewModel() {
    private val client = OkHttpClient()
    private val json: Json = Json { ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true }

    private val _postResult = MutableLiveData<Boolean>()
    val postResult: LiveData<Boolean> get() = _postResult
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun publishFullPost(token: String, userId: Int, content: String, privacy: Int, files: List<File>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uploadedImageUrls = if (files.isNotEmpty()) {
                    uploadImagesSuspend(token,files)
                } else {
                    emptyList()
                }
                val newPostId = createPostSuspend(token, userId, content, privacy)
                Log.d("Test", "Vô 1")
                if (newPostId != null) {
                    Log.d("Test", "Vô 2")
                    uploadedImageUrls.forEach { imageUrl ->
                        Log.d("Test", "Vô 3")
                        createPostMediaSuspend(token, newPostId, imageUrl, 1)
                    }
                    _postResult.postValue(true)
                } else {
                    Log.d("Test", "Vô 4")
                    _postResult.postValue(false)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _postResult.postValue(false)
            }
        }
    }


    private fun uploadImagesSuspend(token: String, files: List<File>): List<String> {
        val urls = mutableListOf<String>()
        try {
            val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            for (file in files) {
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                multipartBuilder.addFormDataPart("images", file.name, requestFile)
            }
            val request = Request.Builder()
                .url("http://10.0.2.2:8989/api/images/upload")
                .addHeader("Authorization", "Bearer $token")
                .post(multipartBuilder.build())
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    val json = JSONObject(bodyString ?: "")
                    if(json.has("files")) {
                        val filesArray = json.getJSONArray("files")
                        for (i in 0 until filesArray.length()) {
                            urls.add(filesArray.getString(i))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return urls
    }

    private fun createPostSuspend(token: String, userId: Int, content: String, privacy: Int): Int? {
        Log.d("Test", "--- BẮT ĐẦU createPostSuspend ---")
        try {
            val reportBody = JSONObject()
                .put("user_id", userId)
                .put("content", content)
                .put("privacy", privacy)
                .put("location_id", 1)
                .put("status", 1)
                .toString()
            Log.d("token", "create ${token}")
            val mediaType = "application/json;charset=utf-8".toMediaType()
            val requestBody = reportBody.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("http://10.0.2.2:8989/api/posts")
                .addHeader("Authorization", "Bearer $token")

                .post(requestBody)
                .build()
            Log.d("token", "create tmp ${token}")
            Log.d("Test", "Đang gửi request tạo bài viết...")

            client.newCall(request).execute().use { resp ->
                Log.d("Test", "Response Code: ${resp.code}")
                Log.d("token", "create ${requestBody}")
                if (resp.isSuccessful) {
                    val bodyString = resp.body?.string().orEmpty()
                    Log.d("Test", "Response Body: $bodyString")

                    // Coi chừng lỗi Parse JSON ở đây nếu PostPostModel không khớp
                    val data = json.decodeFromString<ResponsePostPostModel>(bodyString)
                    Log.d("Test", "ID bài viết mới: ${data.data.id}")
                    return data.data.id
                } else {
                    Log.d("Test", "Request thất bại: ${resp.message}")
                    if (!resp.isSuccessful) {
                        if(resp.code == 403){
                            _error.postValue("TOKEN_EXPIRED")
                        }
                        return@use
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Test", "LỖI CRASH trong createPostSuspend: ${e.message}")
            e.printStackTrace()
        }
        return null
    }

    private fun createPostMediaSuspend(token: String, postId: Int, imageUrl: String, mediaType: Int) {
        try {
            val reportBody = JSONObject().apply {
                put("post_id", postId)
                put("media_url", imageUrl)
                put("media_type", mediaType)
                put("thumbnail_url", "")
            }.toString()

            val reqType = "application/json;charset=utf-8".toMediaType()
            val requestBody = reportBody.toRequestBody(reqType)
            Log.d("Test", "Vô 5")
            val request = Request.Builder()
                .url("http://10.0.2.2:8989/api/post-medias")
                .addHeader("Authorization", "Bearer $token")
                .post(requestBody)
                .build()
            client.newCall(request).execute().use {
                    resp ->
                if (!resp.isSuccessful) {
                    Log.d("Test", "Request thất bại: ${resp.message}")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}